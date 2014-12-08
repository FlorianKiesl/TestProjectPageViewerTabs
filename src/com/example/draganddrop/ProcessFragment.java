package com.example.draganddrop;


import com.example.testprojectprocesstabs.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.ClipData;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProcessFragment extends Fragment {

	final String MAINSTACK = "MAINSTACK", SIDESTACK = "SIDESTACK",
			MSGCARD = "MSGCARD", TASKCARD = "TASKCARD";
	private Process process;
	private View fragment;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment = inflater.inflate(R.layout.fragment_process, container, false);
		
		LinearLayout ll_Task = (LinearLayout) fragment.findViewById(R.id.LayoutWorkCard);
		LinearLayout ll_Msg = (LinearLayout) fragment.findViewById(R.id.LayoutMsgCard);

		LinearLayout ll_MainStack = (LinearLayout) fragment.findViewById(R.id.LayoutMainStack);
		LinearLayout ll_SideStack = (LinearLayout) fragment.findViewById(R.id.LayoutSideStack);
		ImageView iv_bin = (ImageView) fragment.findViewById(R.id.imageViewBin);

		// set tag definitions
		ll_Task.setTag(TASKCARD);
		ll_Msg.setTag(MSGCARD);
		ll_MainStack.setTag(MAINSTACK);
		ll_SideStack.setTag(SIDESTACK);

		// create a new process
		process = new Process("my test process");

		// set touch listeners
		ll_Task.setOnTouchListener(new ChoiceTouchListener());
		ll_Msg.setOnTouchListener(new ChoiceTouchListener());

		// set drag listeners
		ll_MainStack.setOnDragListener(new ChoiceDragListener());
		iv_bin.setOnDragListener(new BinDragListener());
		
		return fragment;
	}
	
	public void addContextInformation(View v) {

	}
	
	public void showContextInformation(View v) {

	}

	private final class ChoiceTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				/*
				 * Drag details: we only need default behavior - clip data could
				 * be set to pass data as part of drag - shadow can be tailored
				 */
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
						view);
				// start dragging the item touched
				view.startDrag(data, shadowBuilder, view, 0);
				return true;
			} else {
				return false;
			}
		}
	}

	private final class DoNothingTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			return true;
		}
	}

	private final class DoNothinDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			return false;
		}
	}

	private final class ChoiceDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// no action necessary
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				// no action necessary
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				// no action necessary
				break;
			case DragEvent.ACTION_DROP:

				// get card (dragged element)
				View view = (View) event.getLocalState();
				LinearLayout dropElement = (LinearLayout) view;

				// get the stack (drag target element)
				LinearLayout targetElement = (LinearLayout) v;

				// is this a card of a stack or a ney one?
				String dropTag = (String) dropElement.getTag();
				String targetTag = (String) targetElement.getTag();
				if (dropTag.equals(MSGCARD) || dropTag.equals(TASKCARD)) {

					// new card
					Card dataCard;
					if (dropTag.equals(MSGCARD)) {
						dataCard = new Message("a message");
					} else {
						dataCard = new Task("a simple task");
					}

					if (!process.addCard(dataCard)) {
						// todo: set alert
					}

				} else if (dropTag.equals(MAINSTACK)
						&& targetTag.equals(SIDESTACK)) {

					// card from main stack --> move it to side stack
					if (!process.putCardAside()) {
						// todo: set alter
					}
				} else if (dropTag.equals(SIDESTACK)
						&& targetTag.equals(MAINSTACK)) {

					// card from main stack --> move it to side stack
					if (!process.putBackFromAside()) {
						// todo: set alter
					}
				}

				updateView();

				break;
			case DragEvent.ACTION_DRAG_ENDED:
				// no action necessary
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	private final class BinDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// no action necessary
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				// no action necessary
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				// no action necessary
				break;
			case DragEvent.ACTION_DROP:

				// get card (dragged element)
				View view = (View) event.getLocalState();
				LinearLayout dropElement = (LinearLayout) view;

				//todo: set question: do you really want to delete the card?
				String dropTag = (String) dropElement.getTag();
				if (dropTag.equals(MAINSTACK)) {

					if (!process.removeCardFromMainStack()) {
						// todo: set alter
					}
				} else if (dropTag.equals(SIDESTACK)) {

					if (!process.removeCardFromSideStack()) {
						// todo: set alter
					}
				}

				updateView();

				break;
			case DragEvent.ACTION_DRAG_ENDED:
				// no action necessary
				break;
			default:
				break;
			}
			return true;
		}
	}

	private void updateView() {

		TextView tv_processTitle = (TextView) fragment.findViewById(R.id.textViewProcessTitle);
		tv_processTitle.setText(process.getTitle());

		// display main and side stack
		TextView tv_mainstack = (TextView) fragment.findViewById(R.id.textViewMainStack);
		TextView tv_sideStack = (TextView) fragment.findViewById(R.id.textViewSideStack);
		
		LinearLayout ll_MainStack = (LinearLayout) fragment.findViewById(R.id.LayoutMainStack);
		LinearLayout ll_SideStack = (LinearLayout) fragment.findViewById(R.id.LayoutSideStack);

		if (process.isMainStackEmpty()) {

			tv_mainstack.setText(R.string.emty_main_stack);
			tv_mainstack.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.stack));
			ll_MainStack.setOnTouchListener(new DoNothingTouchListener());
			ll_SideStack.setOnDragListener(new DoNothinDragListener());
		} else {

			tv_mainstack.setText(process.getMainStackTitle());
			tv_mainstack.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.message));
			ll_MainStack.setOnTouchListener(new ChoiceTouchListener());
			ll_SideStack.setOnDragListener(new ChoiceDragListener());
		}

		if (process.isSideStackEmpty()) {

			tv_sideStack.setText(R.string.emty_side_stack);
			tv_sideStack.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.stack));
			ll_SideStack.setOnTouchListener(new DoNothingTouchListener());

		} else {

			tv_sideStack.setText(process.getSideStackTitle());
			tv_sideStack.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.message));
			ll_SideStack.setOnTouchListener(new ChoiceTouchListener());
		}
	}
}
