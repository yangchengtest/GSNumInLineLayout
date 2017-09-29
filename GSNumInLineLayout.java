package com.suning.mobile.goldshopkeeper.gsmyinfo.customview;

/**
 * Created by 17040682 on 2017/4/25.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.suning.mobile.goldshopkeeper.R;

import java.util.ArrayList;
import java.util.List;

public class GSNumInLineLayout extends ViewGroup {

    private Context mContext;
    private int usefulWidth; // the space of a line we can use(line's width minus the sum of left and right padding
    private int lineSpacing = 0; // the spacing between lines in flowlayout
    List<View> childList = new ArrayList();
    List<Integer> lineNumList = new ArrayList();
    //记录每行的高度。
    List<Integer> lineHeightList = new ArrayList<>();
    private int nums = 4;
    //元素布局方式。
    private int gravity = CENTER;
    public static int CENTER = 0;
    public static int TOP = 1;
    public static int BOTTOM = 2;

    public GSNumInLineLayout(Context context) {
        this(context, null);
    }

    public GSNumInLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GSNumInLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.FlowLayout);
        lineSpacing = mTypedArray.getDimensionPixelSize(
                R.styleable.FlowLayout_lineSpacing, 0);
        mTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();
        int mPaddingBottom = getPaddingBottom();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int lineUsed = mPaddingLeft + mPaddingRight;
        int lineY = mPaddingTop;
        int lineHeight = 0;
        int lineCount = 0;
        lineHeightList.clear();
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int spaceWidth = 0;
            int spaceHeight = 0;
            lineCount++;
            LayoutParams childLp = child.getLayoutParams();
            if (childLp instanceof MarginLayoutParams) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, lineY);
                MarginLayoutParams mlp = (MarginLayoutParams) childLp;
                spaceWidth = mlp.leftMargin + mlp.rightMargin;
                spaceHeight = mlp.topMargin + mlp.bottomMargin;
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            spaceWidth += childWidth;
            spaceHeight += childHeight;

            if ((lineUsed + spaceWidth > widthSize) || (lineCount > nums)) {
                //approach the limit of width and move to next line
                lineY += lineHeight + lineSpacing;
                lineUsed = mPaddingLeft + mPaddingRight;
                lineHeightList.add(lineHeight);
                lineHeight = 0;
                lineCount = 0;
            }
            if (spaceHeight > lineHeight) {
                lineHeight = spaceHeight;
            }
            lineUsed += spaceWidth;
        }
        setMeasuredDimension(
                widthSize,
                heightMode == MeasureSpec.EXACTLY ? heightSize : lineY + lineHeight + mPaddingBottom
        );
        lineHeightList.add(lineHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();

        int lineX = mPaddingLeft;
        int lineY = mPaddingTop;
        int lineWidth = r - l;
        usefulWidth = lineWidth - mPaddingLeft - mPaddingRight;
        int lineUsed = mPaddingLeft + mPaddingRight;
        int lineHeight = 0;
        int lineNum = 0;
        int perLineCount = 0;
        int lineCount = 0;
        lineNumList.clear();
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            perLineCount++;
            int spaceWidth = 0;
            int spaceHeight = 0;
            int left = 0;
            int top = 0;
            int right = 0;
            int bottom = 0;
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            LayoutParams childLp = child.getLayoutParams();
            if (childLp instanceof MarginLayoutParams) {
                MarginLayoutParams mlp = (MarginLayoutParams) childLp;
                spaceWidth = mlp.leftMargin + mlp.rightMargin;
                spaceHeight = mlp.topMargin + mlp.bottomMargin;
                left = lineX + mlp.leftMargin;
                top = lineY + mlp.topMargin;
                right = lineX + mlp.leftMargin + childWidth;
                bottom = lineY + mlp.topMargin + childHeight;
            } else {
                left = lineX;
                top = lineY;
                right = lineX + childWidth;
                bottom = lineY + childHeight;
            }
            spaceWidth += childWidth;
            spaceHeight += childHeight;

            if ((lineUsed + spaceWidth > lineWidth) || (perLineCount > nums)) {
                //approach the limit of width and move to next line
                perLineCount = 0;
                lineNumList.add(lineNum);
                lineY += lineHeight + lineSpacing;
                lineUsed = mPaddingLeft + mPaddingRight;
                lineX = mPaddingLeft;
                lineCount++;
                lineHeight = 0;
                lineNum = 0;
                if (childLp instanceof MarginLayoutParams) {
                    MarginLayoutParams mlp = (MarginLayoutParams) childLp;
                    left = lineX + mlp.leftMargin;
                    top = lineY + mlp.topMargin;
                    right = lineX + mlp.leftMargin + childWidth;
                    bottom = lineY + mlp.topMargin + childHeight;
                } else {
                    left = lineX;
                    top = lineY;
                    right = lineX + childWidth;
                    bottom = lineY + childHeight;
                }
            }
            int gap = 0;
            if (null != lineHeightList.get(lineCount)) {
                int lineMaxHeight = lineHeightList.get(lineCount);
                if (CENTER == gravity)
                    gap = (lineMaxHeight - bottom + top + 1) / 2;
                if (BOTTOM == gravity)
                    gap = lineMaxHeight - bottom + top;
            }
            if (gap < 0)
                gap = 0;
            child.layout(left, top + gap, right, bottom + gap);
            lineNum++;
            if (spaceHeight > lineHeight) {
                lineHeight = spaceHeight;
            }
            lineUsed += spaceWidth;
            lineX += spaceWidth;
        }
        // add the num of last line
        lineNumList.add(lineNum);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void setNums(int nums) {
        nums = nums;
    }

    public void setGravity(int inputgravity) {
        if ((CENTER == gravity) || (BOTTOM == gravity) || (TOP == gravity))
            gravity = inputgravity;
    }
}
