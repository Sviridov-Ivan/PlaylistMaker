package com.example.playlistmaker.media.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.ui.viewmodels.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()
    lateinit var confirmDialog: MaterialAlertDialogBuilder // инициализация переменной для использования диалога

    // работа с загрузкой фото из общего хранилища телефона через photo picker и сохранения этого фото в хранилище приложения
    // регистрируем событие, которое вызывает photo picker
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            //обрабатываем событие выбора пользователем фотографии
            if (uri != null) {
                viewModel.onCoverSelected(uri) // подписка на функцию onCoverSelected
                saveImageToPrivateStorage(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.new_playlist)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // установка селектора цвета для hint для EditText
        val hintColor = ContextCompat.getColorStateList(requireContext(), R.color.playlist_hint_color)
        binding.textInputLayoutName.defaultHintTextColor = hintColor
        binding.textInputLayoutDescription.defaultHintTextColor = hintColor

        setupBackHandling() // диалог
        setupCoverClick() // нажатие на место под картинку
        setupTextWatcher() // работа с вводом текста
        observeViewModel() // подписка на состояния
        setupCreateButton() // создание нового плейлиста
    }

    // реализация диалога при нажатии на стрелку назад и при начале заполнения
     private fun setupBackHandling() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.new_playlist_dialog_title))
            .setMessage(getString(R.string.new_playlist_dialog_message))
            .setNegativeButton(getString(R.string.new_playlist_dialog_negative_button)) { dialog, which -> dialog.dismiss() } // закрываем диалог
            .setPositiveButton(getString(R.string.new_playlist_dialog_positive_button)) { dialog, which -> findNavController().navigateUp() }

        // добавление слушателя для обработки нажатия на кнопку Back, проверка shouldShowExitDialog() и реализация диалога на закрытие
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.shouldShowExitDialog()) {
                        confirmDialog.show()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        )

        // возврат на фрагмент Медиа
        binding.arrowBackToMedia.setOnClickListener {
            if (viewModel.shouldShowExitDialog()) { // роверка shouldShowExitDialog()
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupCoverClick() {
        binding.sourcePlaylistImage.setOnClickListener {
            viewModel.onCoverClick() // запускаем photo picker во viewModel в отдельном потоке
        }
    }

    private fun setupTextWatcher() {
        val nameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.onNameChanged(s.toString()) // ввод имени с измен.статуса во viewModel
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.onDescriptionChanged(s.toString()) // ввод имени с измен.статуса во viewModel
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.inputEditTextNamePlaylist.addTextChangedListener(nameWatcher)
        binding.inputEditTextDescriptionPlaylist.addTextChangedListener(descriptionWatcher)
    }

    private fun observeViewModel() {
        // Подписка на состояние UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.buttonAddPlaylist.isEnabled = state.isCreateButtonEnabled // кнопка
                state.coverUri?.let { binding.sourcePlaylistImage.setImageURI(it) } // картинка
            }
        }

        // Подписка на одноразовые события
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is NewPlaylistViewModel.Event.OpenPhotoPicker -> { // PhotoPicker
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    is NewPlaylistViewModel.Event.ShowToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is NewPlaylistViewModel.Event.CloseScreen -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun setupCreateButton() { // создание нового плейлиста
        binding.buttonAddPlaylist.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    // работа с сохранением фото photo picker в хранилище приложения
    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}