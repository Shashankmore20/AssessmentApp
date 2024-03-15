package com.apptmyz.assessment.fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.apptmyz.assessment.R;
import com.apptmyz.assessment.activity.ReviewActivity;
import com.apptmyz.assessment.config.App;
import com.apptmyz.assessment.database.ContestDataBaseHelper;
import com.apptmyz.assessment.helper.TouchImageView;
import com.apptmyz.assessment.model.Question;
import com.apptmyz.assessment.model.QuestionModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment {

    private List<QuestionModel> questionList;
    private static final String QUESTION_INDEX = "question_index";
    public ScrollView mainScroll, queScroll;
    TouchImageView imgQuestion;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, noteLyt;

    public TextView duration;
    public TextView question_text, tvImgQues, btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    ImageView imgZoom;
    int click = 0;
    private int index;
    String wrongAns, rightAns, useranswer;
    ContestDataBaseHelper contestDataBaseHelper;
    private Question question;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public ReviewFragment(List<QuestionModel> questionList) {
        this.questionList = questionList;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            //  mParam2 = getArguments().getString(ARG_PARAM2);
            index = getArguments().getInt(QUESTION_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        btnAnswerA = view.findViewById(R.id.btnAnswerA);
        btnAnswerB = view.findViewById(R.id.btnAnswerB);
        btnAnswerC = view.findViewById(R.id.btnAnswerC);
        btnAnswerD = view.findViewById(R.id.btnAnswerD);
        question_text = view.findViewById(R.id.question_text);

        duration = ((ReviewActivity) getContext()).findViewById(R.id.duration);

        imgQuestion = view.findViewById(R.id.imgQuestion);

        tvImgQues = view.findViewById(R.id.tvImgQues);
        imgZoom = view.findViewById(R.id.imgZoom);
        mainScroll = view.findViewById(R.id.mainScroll);
        queScroll = view.findViewById(R.id.queScroll);
        noteLyt = view.findViewById(R.id.noteLyt);
        layout_A = view.findViewById(R.id.a_layout);
        layout_B = view.findViewById(R.id.b_layout);
        layout_C = view.findViewById(R.id.c_layout);
        layout_D = view.findViewById(R.id.d_layout);

        assert getArguments() != null;
        final QuestionModel questionModel = questionList.get(getArguments().getInt(QUESTION_INDEX));
        Log.d("DDDD", String.valueOf(questionModel));
        contestDataBaseHelper = new ContestDataBaseHelper(getContext());
        question = contestDataBaseHelper.getQuestionByID(questionModel.getTestquestionid());

        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            //Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        imgQuestion.resetZoom();

      /*  options.addAll(question.getQuestion());

        Collections.shuffle(options);
        layout_C.setVisibility(View.VISIBLE);
        layout_D.setVisibility(View.VISIBLE);
        btnOpt1.setGravity(Gravity.NO_GRAVITY);
        btnOpt2.setGravity(Gravity.NO_GRAVITY);*/
        // question_text.setText(Html.fromHtml(Common.list_question.get()));

        duration.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_access_time, 0, 0, 0);
        duration.setText("" + App.getTimeFromLong(question.getDuration()));

        btnAnswerA.setText(Html.fromHtml(question.getChoice1()));
        btnAnswerB.setText(Html.fromHtml(question.getChoice2()));
        btnAnswerC.setText(Html.fromHtml(question.getChoice3()));
        btnAnswerD.setText(Html.fromHtml(question.getChoice4()));
        // question_text.setText(question.getUserAnswer() + ") " + Html.fromHtml(question.getQuestion()));
        question_text.setText("" + Html.fromHtml(question.getQuestion()));


        layout_A.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_B.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_C.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_D.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));


        String wrongAns;
        if (question.getUserAnswer() != null) {
            wrongAns = question.getUserAnswer().trim();
        } else {
            wrongAns = "";
        }

        //rightAns = question.getAnswer();
        useranswer = question.getUserAnswer();

        layout_A.setBackgroundResource(R.drawable.notselected);
        layout_B.setBackgroundResource(R.drawable.notselected);
        layout_C.setBackgroundResource(R.drawable.notselected);
        layout_D.setBackgroundResource(R.drawable.notselected);

        if (question.getUserAnswer() != null) {
            if (useranswer.equalsIgnoreCase("A")) {
                layout_A.setBackgroundResource(R.drawable.wrong_gradient);
            } else if (useranswer.equalsIgnoreCase("B")) {
                layout_B.setBackgroundResource(R.drawable.wrong_gradient);
            } else if (useranswer.equalsIgnoreCase("C")) {
                layout_C.setBackgroundResource(R.drawable.wrong_gradient);
            } else if (useranswer.equalsIgnoreCase("D")) {
                layout_D.setBackgroundResource(R.drawable.wrong_gradient);
            }
        }

        RightAnswerBackgroundSet(question);
        queScroll.scrollTo(0, 0);


        return view;
    }


    public void RightAnswerBackgroundSet(Question question) {
        String answer = question.getAnswer();
        if (answer.equalsIgnoreCase("A")) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);
        } else if (answer.equalsIgnoreCase("B")) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);
        } else if (answer.equalsIgnoreCase("C")) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);
        } else if (answer.equalsIgnoreCase("D")) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static ReviewFragment newInstance(int sectionNumber, List<QuestionModel> questionList) {
        ReviewFragment fragment = new ReviewFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}