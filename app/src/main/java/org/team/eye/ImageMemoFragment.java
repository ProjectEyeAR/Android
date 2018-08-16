package org.team.eye;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.os.ConfigurationCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageMemoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageMemoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageMemoFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private boolean mExpanded;
    private ImageMemo mImageMemo;

    private ConstraintLayout mBackgroundConstraintLayout;
    private ConstraintLayout mCoverConstraintLayout;
    private ValueAnimator mExpandValueAnimator;
    private ValueAnimator mShrinkValueAnimator;

    private OnFragmentInteractionListener mListener;

    public ImageMemoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageMemo Parameter 1.
     * @return A new instance of fragment ImageMemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageMemoFragment newInstance(ImageMemo imageMemo) {
        ImageMemoFragment fragment = new ImageMemoFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARG_PARAM1, imageMemo);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageMemo = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_memo, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpanded = !mExpanded;

                if (mExpanded) {
                    mExpandValueAnimator.start();
                } else {
                    mShrinkValueAnimator.start();
                }
            }
        });
        mBackgroundConstraintLayout = view.findViewById(
                R.id.ImageMemoFragment_BackgroundConstraintLayout
        );
        mCoverConstraintLayout = view.findViewById(R.id.ImageMemoFragment_CoverConstraintLayout);
        ImageView imageView = view.findViewById(R.id.ImageMemoFragment_ImageView);
        TextView filterTextView = view.findViewById(R.id.ImageMemoFragment_FilterTextView);
        TextView usernameTextView = view.findViewById(R.id.ImageMemoFragment_UsernameTextView);
        TextView timestampTextView = view.findViewById(R.id.ImageMemoFragment_TimestampTextView);

        Locale locale = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                locale
        );

        Glide.with(this).load(mImageMemo.getImage()).into(imageView);
        filterTextView.setText(mImageMemo.getImageFilter());
        usernameTextView.setText(mImageMemo.getUser().getUsername());
        timestampTextView.setText(simpleDateFormat.format(mImageMemo.getTimestamp()));

        int defaultMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                64,
                getResources().getDisplayMetrics()
        );
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ConstraintLayout.LayoutParams layoutParams1 =
                                (ConstraintLayout.LayoutParams)
                                        mBackgroundConstraintLayout.getLayoutParams();
                        ConstraintLayout.LayoutParams layoutParams2 =
                                (ConstraintLayout.LayoutParams)
                                        mCoverConstraintLayout.getLayoutParams();
                        layoutParams1.leftMargin = (Integer) animation.getAnimatedValue();
                        layoutParams1.rightMargin = (Integer) animation.getAnimatedValue();
                        layoutParams1.bottomMargin = (Integer) animation.getAnimatedValue();
                        layoutParams2.topMargin = (Integer) animation.getAnimatedValue();

                        mBackgroundConstraintLayout.requestLayout();
                        mCoverConstraintLayout.requestLayout();
                    }
                };
        mExpandValueAnimator = ValueAnimator.ofInt(defaultMargin, 0);
        mShrinkValueAnimator = ValueAnimator.ofInt(0, defaultMargin);

        mExpandValueAnimator.addUpdateListener(animatorUpdateListener);
        mShrinkValueAnimator.addUpdateListener(animatorUpdateListener);
        mExpandValueAnimator.setDuration(300);
        mShrinkValueAnimator.setDuration(300);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
