package com.example.playlistmaker.player.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {


    // Bitmap для состояния PLAY
    private var drawablePlay: Drawable? = null // глобальное переменная - экземпляр Bitmap — это класс, который содержит данные изображения в самом простом из возможных форматов: в виде массива пикселей

    // Bitmap для состояния PAUSE
    private var drawablePause: Drawable? = null

    // Прямоугольник, в который рисует bitmap. Его размеры будут равны размерам View
    private val imageRect = RectF()

    // Текущее UI-состояние кнопки. По умолчанию — PLAY
    private var state: PlaybackButtonState = PlaybackButtonState.PLAY

    // Коллбек клика (назначается во Fragment)
    var onClick: (() -> Unit)? = null

    init {
        // кастомные атрибуты из XML
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomImageViewPlayButton,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                drawablePlay = getDrawable(R.styleable.CustomImageViewPlayButton_imagePlay)

                drawablePause = getDrawable(R.styleable.CustomImageViewPlayButton_imagePause)

            } finally {
                // освобождаем TypedArray
                recycle()
            }
        }

        // View кликабельная и доступной для accessibility
        isClickable = true
        isFocusable = true
    }

    // Размеры вьюхи известны только тут. Rect, в который рисуется bitmap
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        imagePlayRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
//        imagePauseRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    // Публичный метод для обновления состояния кнопки. Вызывается ИЗВНЕ во фрагменте
    fun setState(state: PlaybackButtonState) {
        if (this.state == state) return
        this.state = state
        invalidate()
    }

    // Отрисовка View. Вызывается системой после invalidate()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val drawable = when (state) {
            PlaybackButtonState.PLAY -> drawablePlay
            PlaybackButtonState.PAUSE -> drawablePause
        }

        drawable?.apply {
            setBounds(
                imageRect.left.toInt(),
                imageRect.top.toInt(),
                imageRect.right.toInt(),
                imageRect.bottom.toInt()
            )
            draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Сообщение системе, что обработка необходима на клик
                alpha = 0.8f // эффект нажатия на кнопку:)
                return true
            }
            MotionEvent.ACTION_UP -> {
                alpha = 1f
                performClick()// Изменение состояния кнопки
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // стандартный контракт клика View. вызов onClick-коллбек.
    override fun performClick(): Boolean {
        super.performClick()
        onClick?.invoke()
        return true
    }

}