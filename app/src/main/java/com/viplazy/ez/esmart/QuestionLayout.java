package com.viplazy.ez.esmart;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionLayout {

    public static final int SELECTED_ITEM_1= 1;
    public static final int SELECTED_ITEM_2= 2;
    public static final int SELECTED_ITEM_3= 3;
    public static final int SELECTED_ITEM_4= 4;
    public static final int SELECTED_ITEM_NONE= 0;

    private TextView questionTitle;

    private RadioGroup radioGroupAnswer;

    private ArrayList<TextView> listAnswer;

    private TextView submit;

    private ImageView imageView;

    private ScrollView answer_filed;

    int currentState;

    public ScrollView getAnswer_filed() {
        return answer_filed;
    }

    public void setAnswer_filed(ScrollView answer_filed) {
        this.answer_filed = answer_filed;
    }

    public QuestionLayout(View layoutParent) {

        questionTitle = layoutParent.findViewById(R.id.question_name);

        listAnswer = new ArrayList<>();

        currentState = 0;

        TextView textView;
        textView = layoutParent.findViewById(R.id.answer_tv_1);
        listAnswer.add(textView);

        textView = layoutParent.findViewById(R.id.answer_tv_2);
        listAnswer.add(textView);

        textView = layoutParent.findViewById(R.id.answer_tv_3);
        listAnswer.add(textView);

        textView = layoutParent.findViewById(R.id.answer_tv_4);
        listAnswer.add(textView);

        submit = layoutParent.findViewById(R.id.tv_submit);

        imageView = layoutParent.findViewById(R.id.imgView);

        answer_filed = layoutParent.findViewById(R.id.answer_field);

        for (TextView tv : listAnswer) {

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setCurrentState(view);
                }
            });
        }

    }

    public TextView getQuestionTitle() {

        return questionTitle;
    }

    public void setQuestionTitle(TextView questionTitle) {
        this.questionTitle = questionTitle;
    }

    public RadioGroup getRadioGroupAnswer() {
        return radioGroupAnswer;
    }

    public void setRadioGroupAnswer(RadioGroup radioGroupAnswer) {
        this.radioGroupAnswer = radioGroupAnswer;
    }

    public ArrayList<TextView> getListAnswer() {
        return listAnswer;
    }

    public void setListAnswer(ArrayList<TextView> listAnswer) {
        this.listAnswer = listAnswer;
    }

    public TextView getSubmit() {
        return submit;
    }

    public void setSubmit(TextView submit) {
        this.submit = submit;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getAnswerView(int state) {

        switch (state) {
            case SELECTED_ITEM_1:
                return listAnswer.get(0);
            case SELECTED_ITEM_2:
                return listAnswer.get(1);
            case SELECTED_ITEM_3:
                return listAnswer.get(2);
            case SELECTED_ITEM_4:
                return listAnswer.get(3);

                default: return null;
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setSelectAnswer(int stateAnswer, boolean mode) {

        TextView v = getAnswerView(stateAnswer);

        if (v != null) {
            if (mode) v.setBackgroundResource(R.drawable.rounded_shape_answer_selected);
            else v.setBackgroundResource(R.drawable.rounded_shape_answer);
        }
    }

    public TextView getSelectedView() {

        return getAnswerView(currentState);
    }

    public void setCurrentState(int currentState) {

        setSelectAnswer(this.currentState, false);

        if (currentState != this.currentState || this.currentState == SELECTED_ITEM_NONE) {

            this.currentState = currentState;
            setSelectAnswer(currentState, true);

        }

        // Deselect a answer if double select
        else this.currentState = SELECTED_ITEM_NONE;

        if (this.currentState != SELECTED_ITEM_NONE) submit.setBackgroundResource(R.drawable.rounded_shape_btn_selected);
        else submit.setBackgroundResource(R.drawable.rounded_shape_btn);
    }

    public void setCurrentState(View v) {

        for (int i = 0; i < listAnswer.size(); i++) {
            if (listAnswer.get(i) == v) {
                setCurrentState(SELECTED_ITEM_1 + i);
            }
        }
    }
}
