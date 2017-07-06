package com.alcatel.wifilink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Բ�ν�����
 * 
 * @author yung7086
 *         <p>
 *         2015��12��9�� 20:44:29
 *         <p>
 *         ps: Բ�����Զ����ݿ�ȸ߶�ȡС�ߵĴ�С�ı��� ��Ҳ�����Լ�����Բ���Ĵ�С@see #scrollBy(int, int)
 */
public class CircleProgress extends View {

	private int width;
	private int height;
	private int radius;
	private int socktwidth = dp2px(3);
	private Paint paint = new Paint();
	private Rect rec = new Rect();
	private int value = 75;
	private int textSize = dp2px(70);
	private Bitmap bitmap;
	@Deprecated
	float scale = 0.15f;
	private int preColor = Color.parseColor("#ffffff");
	private int progressColor = Color.parseColor("#10ff90");
	private float paddingscale = 0.8f;
	private int CircleColor = Color.parseColor("#74b3ff");
	private int textColor = progressColor;
	private onProgressListener monProgress;
	private int startAngle = 90;
	RectF rectf = new RectF();

	public CircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		width = getWidth();
		int size = height = getHeight();
		if (height > width)
			size = width;
		radius = (int) (size * paddingscale / 2f) - 20;
		paint.setAntiAlias(true);
		paint.setColor(preColor);
		paint.setAlpha(112);

		canvas.drawCircle(width / 2, height / 2, radius, paint);
		rectf.set((width - radius * 2) / 2f, (height - radius * 2) / 2f,
				((width - radius * 2) / 2f) + (2 * radius),
				((height - radius * 2) / 2f) + (2 * radius));
		paint.setColor(progressColor);
		canvas.drawArc(rectf, startAngle, value * 3.6f, true, paint);
		paint.setColor(CircleColor);

		canvas.drawCircle(width / 2, height / 2, radius - socktwidth, paint);
		if (bitmap != null) {
			int width2 = (int) (rectf.width() * scale);
			int height2 = (int) (rectf.height() * scale);
			rectf.set(rectf.left + width2, rectf.top + height2, rectf.right
					- width2, rectf.bottom - height2);
			canvas.drawBitmap(bitmap, null, rectf, null);
		}
//		String v = value + "%";
//		paint.setColor(textColor);
//		paint.setTextSize(textSize);
//		paint.getTextBounds(v, 0, v.length(), rec);
//		int textwidth = rec.width();
//		int textheight = rec.height();
//		// �����м�����
//		canvas.drawText(v, (width - textwidth) / 2,
//				((height + textheight) / 2), paint);
		super.onDraw(canvas);
	}

	public int dp2px(int dp) {
		return (int) ((getResources().getDisplayMetrics().density * dp) + 0.5);
	}

	/**
	 * ���ý���
	 * 
	 * @param value
	 *            <p>
	 *            ps: �ٷֱ� 0~100;
	 */
	public void setValue(int value) {
		if (value > 100)
			return;
		this.value = value;
		invalidate();
		if (monProgress != null)
			monProgress.onProgress(value);
	}

	/**
	 * ����Բ���������Ŀ�� px
	 */
	public CircleProgress setProdressWidth(int width) {
		this.socktwidth = width;
		return this;
	}

	/**
	 * �������ִ�С
	 * 
	 * @param value
	 */
	public CircleProgress setTextSize(int value) {
		textSize = value;
		return this;
	}

	/**
	 * �������ִ�С
	 * 
	 * @param color
	 */
	public CircleProgress setTextColor(int color) {
		this.textColor = color;
		return this;
	}

	/**
	 * ���ý�����֮ǰ����ɫ
	 * 
	 * @param precolor
	 */
	public CircleProgress setPreProgress(int precolor) {
		this.preColor = precolor;
		return this;
	}

	/**
	 * ���ý�����ɫ
	 * 
	 * @param color
	 */
	public CircleProgress setProgress(int color) {
		this.progressColor = color;
		return this;
	}

	/**
	 * ����Բ���м�ı�����ɫ
	 * 
	 * @param color
	 * @return
	 */
	public CircleProgress setCircleBackgroud(int color) {
		this.CircleColor = color;
		return this;
	}

	/**
	 * ����Բ��������ؼ��Ŀ�Ȼ��߸߶ȵ�ռ�ñ���
	 * 
	 * @param scale
	 */
	public CircleProgress setPaddingscale(float scale) {
		this.paddingscale = scale;
		return this;
	}

	/**
	 * ���ÿ�ʼ��λ��
	 * 
	 * @param startAngle
	 *            0~360
	 *            <p>
	 *            ps 0���������ұ� 90 ���·� ����Ȼ��˳ʱ����ת
	 */
	public CircleProgress setStartAngle(int startAngle) {
		this.startAngle = startAngle;
		return this;
	}

	public interface onProgressListener {
		void onProgress(int value);
	}
}
