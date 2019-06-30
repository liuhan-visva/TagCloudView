package com.biubiustudio.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

public class TagCloudView extends ViewGroup implements Runnable, TagsAdapter.OnDataSetChangeListener {
    private Handler EndHandler = new Handler(Looper.getMainLooper());
    private Handler StarHandler = new Handler(Looper.getMainLooper());
    private Point direction = Point.make(0.0F, -1F, 0.0F);
    private GestureDetector gestureDetector;
    boolean isRun = true;
    private float lastX;
    private float lastY;
    private MarginLayoutParams layoutParams;
    private TagCloud mTagCloud;
    private VelocityTracker mVelocityTracker = null;
    private int minSize;
    private OnTagClickListener onTagClickListener;
    private TagsAdapter tagsAdapter = new NOPTagsAdapter();
    private double velocity;
    private ArrayList<Point> points;

    public TagCloudView(Context paramContext) {
        super(paramContext);
        init(paramContext, null);
    }

    public TagCloudView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext, paramAttributeSet);
    }

    public TagCloudView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    private void addListener(View paramView, final int paramInt) {
        if (!paramView.hasOnClickListeners() && this.onTagClickListener != null) {
            paramView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    TagCloudView.this.onTagClickListener.onItemClick(TagCloudView.this, view, paramInt);
                }
            });
        }
    }

    private void addVelocityTrackerEvent(MotionEvent paramMotionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(paramMotionEvent);
    }

    private void animateOpenView() {
        ValueAnimator localValueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        localValueAnimator.setDuration(400L);
        localValueAnimator.setInterpolator(new DecelerateInterpolator());
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animator) {
                float f = (float) animator.getAnimatedValue();
                TagCloudView.this.setScaleX(f);
                TagCloudView.this.setScaleY(f);
            }
        });
        localValueAnimator.start();
    }

    private void autoTurnRotation() {
        final int size = points.size();
        for (int i = 0; i < size; i++) {
            updateFrameOfPoint(i, this.direction, 0.002F);
        }
    }

    private int getTouchVelocityX() {
        if (this.mVelocityTracker == null) {
            return 0;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return Math.abs((int) this.mVelocityTracker.getXVelocity());
    }

    private int getTouchVelocityY() {
        if (this.mVelocityTracker == null) {
            return 0;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return Math.abs((int) this.mVelocityTracker.getYVelocity());
    }

    private void inertiaStart() {
        timerStop();
        this.EndHandler.post(this);
    }

    private void inertiaStep() {
        if (Double.isNaN(this.velocity)) {
            this.velocity = 5000.0F;
        }
        if (this.velocity <= 0.0F) {
            inertiaStop();
            return;
        }
        this.velocity -= 120.0F;
        float f = (float) (this.velocity / getWidth() * 16.0F / 1000.0F);
        final int size = points.size();
        for (int i = 0; i < size; i++) {
            updateFrameOfPoint(i, this.direction, f);
        }
    }

    private void inertiaStop() {
        timerStart();
        this.EndHandler.removeCallbacksAndMessages(null);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        setFocusableInTouchMode(true);
        this.mTagCloud = new TagCloud();
        this.points = new ArrayList<>();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        android.graphics.Point point = new android.graphics.Point();
        wm.getDefaultDisplay().getSize(point);
        this.minSize = point.x;
        if (point.y < point.x) {
            this.minSize = point.y;
        }
    }

    private void positionView(int width, Point point, View child) {
        float x = point.x;
        float halfWidth = width / 2.0F;
        width = (int) ((x + 1.0F) * halfWidth) - child.getMeasuredWidth() / 2;
        int i = (int) ((point.y + 1.0F) * halfWidth) - child.getMeasuredHeight() / 2;
        child.layout(width, i, child.getMeasuredWidth() + width, child.getMeasuredHeight() + i);
        halfWidth = (float) Math.sin(point.z * Math.PI / 2.0D);
        x = halfWidth;
        if (halfWidth < 0.3D) {
            x = 0.3F;
        }
        child.setScaleX(x);
        child.setScaleY(x);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            child.setZ(x);
        }
        child.setAlpha(x);
    }

    private void resetChildren() {
        removeAllViews();
        for (Tag tag : mTagCloud.getTagList()) {
            this.addView(tag.getView());
        }

        this.points.clear();
        int childCount = this.getChildCount();
        float factor = (float) ((3 - Math.sqrt(5)) * Math.PI);
        float halfCount = 2.0F / childCount;
        for (int index = 0; index < childCount; ++index) {
            float ii = index * halfCount - 1f + halfCount / 2f;
            double indexFactor = (double) (index * factor);
            double v11 = Math.sqrt(1f - ii * ii);
            Point point = Point.make((float) (Math.cos(indexFactor) * v11), ii, (float) (Math.sin(indexFactor) * v11));
            points.add(point);
            positionView(getWidth(), point, getChildAt(index));
        }
    }

    private void setTagOfPoint(Point paramPoint, int paramInt) {
        View localView = getChildAt(paramInt);
        positionView(getWidth(), paramPoint, localView);
    }

    private void timerStart() {
        this.isRun = true;
    }

    private void timerStop() {
        this.isRun = false;
    }

    private void updateFrameOfPoint(int index, Point point, float angle) {
        point = Matrix.pointRotation(points.get(index), point, angle);
        this.points.set(index, point);
        setTagOfPoint(point, index);
    }

    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(paramMotionEvent);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.StarHandler.post(new Runnable() {
            public void run() {
                StarHandler.postDelayed(this, 16L);
                if (!TagCloudView.this.isRun) {
                    return;
                }
                TagCloudView.this.timerStart();
                TagCloudView.this.autoTurnRotation();
            }
        });
        this.gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            public boolean onDown(MotionEvent paramAnonymousMotionEvent) {
                return false;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                TagCloudView.this.velocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
                TagCloudView.this.inertiaStart();
                TagCloudView.this.timerStop();
                return false;
            }

            public void onLongPress(MotionEvent paramAnonymousMotionEvent) {
            }

            public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2) {
                return false;
            }

            public void onShowPress(MotionEvent paramAnonymousMotionEvent) {
            }

            public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent) {
                return false;
            }
        });
    }

    public void onChange() {
        this.mTagCloud.clear();
        final int childCount = tagsAdapter.getCount();
        for (int i = 0; i < childCount; i++) {
            Tag localTag = new Tag(tagsAdapter.getPopularity(i));
            View localView = tagsAdapter.getView(getContext(), i, this);
            localTag.setView(localView);
            this.mTagCloud.add(localTag);
            addListener(localView, i);
        }
        resetChildren();
        animateOpenView();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isRun = false;
        this.EndHandler.removeCallbacksAndMessages(null);
        this.StarHandler.removeCallbacksAndMessages(null);
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float lastX = this.lastX;
                float lastY = this.lastY;
                if (Math.abs(x - lastX) + Math.abs(y - lastY) > 10.0F) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                this.lastX = x;
                this.lastY = y;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.points.clear();
        final int childCount = getChildCount();
        float factor = (float) ((3.0D - Math.sqrt(5.0D)) * Math.PI);
        float halfChildCountReverse = (float) (2.0D / childCount);
        for (int i = 0; i < childCount; i++) {
            float f1 = i * halfChildCountReverse - 1.0F + halfChildCountReverse / 2.0F;
            float f2 = (float) Math.sqrt(1.0F - f1 * f1);
            double indexFactor = i * factor;
            double indexFactorCos = Math.cos(indexFactor);
            Point localPoint = Point.make((float) (indexFactorCos * f2), f1, (float) (Math.sin(indexFactor) * f2));
            this.points.add(localPoint);
            View localView = getChildAt(l);
            positionView(r, localPoint, localView);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int childCount = this.getChildCount();
        int widthRemainSize = widthSize - getPaddingLeft() - getPaddingRight();
        this.measureChildren(widthMeasureSpec, heightMeasureSpec);
        int countChildHeight = 0;
        int lastChildHeight = 0;
        int lastChildWith = 0;
        int lastWidthSize = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != View.GONE) {
                if (child.getMeasuredWidth() + lastChildWith > widthRemainSize) {
                    countChildHeight += lastChildHeight;
                    lastWidthSize = widthSize;
                    lastChildWith = child.getMeasuredWidth();
                    lastChildHeight = child.getMeasuredHeight();
                } else {
                    lastChildWith += child.getMeasuredWidth();
                }

                lastChildHeight = Math.max(lastChildHeight, child.getMeasuredHeight());
            }
        }
        setMeasuredDimension(TagCloudView.resolveSize(Math.max(lastChildWith, lastWidthSize) + getPaddingLeft() + getPaddingRight(), widthMeasureSpec), TagCloudView.resolveSize(Math.max(countChildHeight + lastChildHeight, lastChildHeight) + getPaddingBottom() + getPaddingTop(), heightMeasureSpec));
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                this.direction.x = (this.lastY - y);
                this.direction.y = (x - this.lastX);
                this.direction.z = 0.0F;
                float ratio = (float) Math.sqrt(this.direction.x * this.direction.x + this.direction.y * this.direction.y) / getWidth();
                for (int i = 0; i < this.points.size(); i++) {
                    updateFrameOfPoint(i, this.direction, ratio * 2.0F);
                }
                this.lastX = x;
                this.lastY = y;
                return true;
            case MotionEvent.ACTION_DOWN:
                timerStop();
                inertiaStop();
                this.lastX = event.getX();
                this.lastY = event.getY();
                return true;
            default:
                break;
        }
        return true;
    }

    public void onUserVisible(boolean visible) {
        if (visible) {
            this.isRun = false;
            this.EndHandler.removeCallbacksAndMessages(null);
            this.isRun = true;
        } else {
            this.isRun = false;
            this.EndHandler.removeCallbacksAndMessages(null);
        }

    }

    public void run() {
        this.EndHandler.post(this);
        inertiaStep();
    }

    public final void setAdapter(TagsAdapter paramTagsAdapter) {
        this.tagsAdapter = paramTagsAdapter;
        this.tagsAdapter.setOnDataSetChangeListener(this);
        onChange();
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.onTagClickListener = listener;
    }

    public interface OnTagClickListener {
        void onItemClick(ViewGroup parent, View child, int position);
    }
}
