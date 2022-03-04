package example.app_images_hw4

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.properties.Delegates


class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    View(context, attrs, defStyleAttr, defStyleRes) {

    private var nowAction = AnimationType.CIRCLE_CLOSE
    private var maxSize = -1
    private var minSize = -1
    private val borderSize = 5
    private var nowSizeCircle = -1
    private var nowSizeRect = -1
    private var circleCenterX by Delegates.notNull<Float>()
    private var rectCenterX by Delegates.notNull<Float>()
    private var circleCenterY by Delegates.notNull<Float>()
    private var rectCenterY by Delegates.notNull<Float>()
    private var circleRadius by Delegates.notNull<Float>()
    private var mCirclePaint: Paint
    private var outCirclePaint: Paint


    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MyView, defStyleAttr, defStyleRes
        )
        try {
            mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            outCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mCirclePaint.color = Color.parseColor(COLOR_HEX_CODE)
            outCirclePaint.color = Color.BLACK
        } finally {
            a.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(w, h)

        maxSize = size / 4
        nowSizeCircle = maxSize
        nowSizeRect = maxSize
        minSize = maxSize / 2
        setMeasuredDimension(size * 2, size * 2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        circleCenterX = w / 2f
        circleCenterY = h * 3 / 4f
        rectCenterX = w / 2f
        rectCenterY = h / 4f
        circleRadius = min(w, h) / 4f
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (nowAction) {
            AnimationType.CIRCLE_CLOSE -> circleClose()
            AnimationType.CIRCLE_OPEN -> circleOpen()
            AnimationType.RECT_CLOSE -> rectClose()
            AnimationType.RECT_OPEN -> rectOpen()
        }

        invalidate()
        drawingAction(canvas)

    }


    private fun drawingAction(canvas: Canvas?) {
        canvas!!.drawCircle(circleCenterX, circleCenterY, nowSizeCircle.toFloat(), outCirclePaint)
        canvas.drawCircle(
            circleCenterX,
            circleCenterY,
            (nowSizeCircle - borderSize).toFloat(),
            mCirclePaint
        )
        canvas.drawRect(
            (rectCenterX - nowSizeRect - borderSize),
            (rectCenterY + nowSizeRect + borderSize),
            (rectCenterX + nowSizeRect + borderSize),
            (rectCenterY - nowSizeRect - borderSize), outCirclePaint
        )
        canvas.drawRect(
            (rectCenterX - nowSizeRect),
            (rectCenterY + nowSizeRect),
            (rectCenterX + nowSizeRect),
            (rectCenterY - nowSizeRect), mCirclePaint
        )
    }

    private fun circleClose() {
        nowSizeCircle--
        if (nowSizeCircle <= minSize) nowAction = AnimationType.RECT_CLOSE
    }

    private fun circleOpen() {
        nowSizeCircle++
        if (nowSizeCircle >= maxSize) nowAction = AnimationType.RECT_OPEN
    }

    private fun rectClose() {
        nowSizeRect--
        if (nowSizeRect <= minSize) nowAction = AnimationType.CIRCLE_OPEN
    }

    private fun rectOpen() {
        nowSizeRect++
        if (nowSizeRect >= maxSize) nowAction = AnimationType.CIRCLE_CLOSE
    }

    enum class AnimationType {
        CIRCLE_CLOSE, RECT_CLOSE, CIRCLE_OPEN, RECT_OPEN;
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putInt(MAX_SIZE, maxSize)
        bundle.putInt(MIN_SIZE, minSize)
        bundle.putInt(NOW_SIZE_CIRCLE, nowSizeCircle)
        bundle.putInt(NOW_SIZE_RECT, nowSizeRect)
        bundle.putFloat(CIRCLE_CENTER_X, circleCenterX)
        bundle.putFloat(RECT_CENTER_X, rectCenterX)
        bundle.putFloat(CIRCLE_CENTER_Y, circleCenterY)
        bundle.putFloat(RECT_CENTER_Y, rectCenterY)
        bundle.putFloat(CIRCLE_RADIUS, circleRadius)
        bundle.putString(NOW_ACTION, nowAction.name)
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bundle = state as Bundle
        maxSize = bundle.getInt(MAX_SIZE)
        minSize = bundle.getInt(MIN_SIZE)
        nowSizeCircle = bundle.getInt(NOW_SIZE_CIRCLE)
        nowSizeRect = bundle.getInt(NOW_SIZE_RECT)
        circleCenterX = bundle.getFloat(CIRCLE_CENTER_X)
        rectCenterX = bundle.getFloat(RECT_CENTER_X)
        circleCenterY = bundle.getFloat(CIRCLE_CENTER_Y)
        rectCenterY = bundle.getFloat(RECT_CENTER_Y)
        circleRadius = bundle.getFloat(CIRCLE_RADIUS)
        nowAction = AnimationType.valueOf(bundle.getString(NOW_ACTION).toString())

        super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE))
    }


}