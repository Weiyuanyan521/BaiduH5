package com.haokan.screen.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MarginDecoration extends RecyclerView.ItemDecoration {
	private int leftmargin;
	private int topmargin;
	private int rightmargin;
	private int bottommargin;

	public MarginDecoration(Context context, int id) {
		bottommargin = rightmargin = topmargin = leftmargin = context.getResources().getDimensionPixelSize(id);
	}

	public MarginDecoration(Context context, int leftOrrightid, int topOrbottomid) {
		leftmargin = rightmargin = context.getResources().getDimensionPixelSize(leftOrrightid);
		topmargin = bottommargin = context.getResources().getDimensionPixelSize(topOrbottomid);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		outRect.set(leftmargin, topmargin, rightmargin, bottommargin);
	}
}
