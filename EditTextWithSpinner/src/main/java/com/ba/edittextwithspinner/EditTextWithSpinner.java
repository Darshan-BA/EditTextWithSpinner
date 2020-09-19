package com.ba.edittextwithspinner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditTextWithSpinner extends LinearLayout {
    private String hint;
    private int inputType;
    private Drawable drawable;
    private ArrayList<String> spinnerArrayItems =new ArrayList<>();
    private LayoutInflater layoutInflater;
    private LinearLayout containerLayout;
    private List<FieldViewHolder> fieldViewHolders = new ArrayList<>();
    private String dateOrTime = null;
    private String spinnerPosition;
    private boolean addCustomSpinnerItem;

    public EditTextWithSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.layout_view_container, this, true);
        containerLayout = findViewById(R.id.container);
        processAttributes(context, attrs);
        addViews();
    }
    private void processAttributes(Context context, @Nullable AttributeSet attrs) {
        final TypedArray styleAttributesArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        hint = styleAttributesArray.getString(R.styleable.CustomEditText_android_hint);
        inputType = styleAttributesArray.getInt(R.styleable.CustomEditText_android_inputType, 0);
        CharSequence[] sequence = styleAttributesArray.getTextArray(R.styleable.CustomEditText_android_entries);
        drawable = styleAttributesArray.getDrawable(R.styleable.CustomEditText_android_drawable);
        dateOrTime = styleAttributesArray.getString(R.styleable.CustomEditText_dateOrTime);
        spinnerPosition = styleAttributesArray.getString(R.styleable.CustomEditText_spinnerPosition);
        addCustomSpinnerItem = styleAttributesArray.getBoolean(R.styleable.CustomEditText_addCustomSpinnerItem, false);
        if (sequence != null) {
            for (CharSequence s : sequence) {
                spinnerArrayItems.add(String.valueOf(s));
            }
            if (addCustomSpinnerItem)
                spinnerArrayItems.add("Custom");
        }
        styleAttributesArray.recycle();
    }

    private void addViews() {
        View inflatedView;
        if (spinnerPosition == null) {
            inflatedView = layoutInflater.inflate(R.layout.layout_edit_text_spinner, containerLayout, false);
        } else {
            if (spinnerPosition.equals("side")) {
                inflatedView = layoutInflater.inflate(R.layout.layout_edit_text_spinner_sideview, containerLayout, false);
            } else {
                inflatedView = layoutInflater.inflate(R.layout.layout_edit_text_spinner, containerLayout, false);
            }
        }
        containerLayout.addView(inflatedView);

        FieldViewHolder fieldViewHolder;
        if (containerLayout.getChildCount() >= (spinnerArrayItems.size() - 1) && addCustomSpinnerItem) {
            fieldViewHolder = new FieldViewHolder(inflatedView, hint, inputType, spinnerArrayItems.size() - 1, drawable);
        } else if (containerLayout.getChildCount() >= spinnerArrayItems.size()) {
            fieldViewHolder = new FieldViewHolder(inflatedView, hint, inputType, spinnerArrayItems.size(), drawable);
        } else {
            fieldViewHolder = new FieldViewHolder(inflatedView, hint, inputType, containerLayout.getChildCount(), drawable);
        }
        fieldViewHolders.add(fieldViewHolder);
    }


    public List<FieldData> getTypeAndData () {
        List<FieldData> fieldData = new ArrayList<>();
        for (FieldViewHolder fieldViewHolder : fieldViewHolders) {
            if (!fieldViewHolder.getEditText().isEmpty()) {
                FieldData data = new FieldData(fieldViewHolder.getType(), fieldViewHolder.getEditText());
                fieldData.add(data);
            }
        }
        return fieldData;
    }


    private class FieldViewHolder {

        public TextInputLayout editLayout;
        public TextInputEditText editText;
        public EditSpinner editSpinner;
        private int type;
        private ArrayList<String> mSpinnerItems;

        public FieldViewHolder(final View itemView, String hint, int inputType, int selectItem, Drawable drawable) {
            type = selectItem - 1;
            mSpinnerItems=spinnerArrayItems;
            editLayout = itemView.findViewById(R.id.compound_editTextLayout);
            editLayout.setHint(hint);
            editLayout.setStartIconDrawable(drawable);
            editLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            editText = itemView.findViewById(R.id.compound_editText);
            if (dateOrTime != null && drawable == null) {
                editText.setFocusable(false);
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_drop_down_arrow, 0);
                //editText.setOnTouchListener(onTouchListener);
                editText.setOnClickListener(onClickListener);
            } else {
                editText.setInputType(inputType);
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
            editSpinner = itemView.findViewById(R.id.compound_editSpinner);
            final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,mSpinnerItems);
            editSpinner.setAdapter(arrayAdapter);
            editSpinner.selectItem(selectItem - 1);
            editSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (position == arrayAdapter.getPosition("Custom")) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Custom name");
                        final EditText et = new EditText(getContext());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                        et.setLayoutParams(lp);
                        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        et.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        builder.setView(et);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrayAdapter.insert(et.getText().toString().trim(),(position));
                                arrayAdapter.notifyDataSetChanged();
                                editSpinner.selectItem( position);
                                type=position;
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                editSpinner.selectItem(0);
                            }
                        });
                        builder.create().show();
                    }else
                        type = position;

                }
            });

            final boolean[] init = {true};
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0 && init[0]) {
                        addViews();
                        init[0] = false;
                    }
                    if (editable.length() == 0 && !init[0]) {
                        init[0] = true;
                        for (int i = 0; i < fieldViewHolders.size(); i++) {
                            if (fieldViewHolders.get(i).getEditText().isEmpty()) {
                                containerLayout.removeViewAt(i);
                                fieldViewHolders.remove(i);
                                break;
                            }
                        }
                    }
                }
            });
        }
        private OnClickListener onClickListener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int mYear = calendar.get(Calendar.YEAR);
                final int mMonth = calendar.get(Calendar.MONTH);
                final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int mMinute = calendar.get(Calendar.MINUTE);
                switch (dateOrTime) {
                    case "date": {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                editText.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        break;
                    }
                    case "time": {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay > 12)
                                    editText.setText(hourOfDay - 12 + ":" + minute + " " + "PM");
                                else if (hourOfDay == 12)
                                    editText.setText(hourOfDay + ":" + minute + " " + "PM");
                                else
                                    editText.setText(hourOfDay + ":" + minute + " " + "AM");
                            }
                        }, mHour, mMinute, false);
                        timePickerDialog.show();
                        break;
                    }
                }
            }
        };

        private OnTouchListener onTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (editText.getRight() -
                        editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Calendar calendar = Calendar.getInstance();
                    final int mYear = calendar.get(Calendar.YEAR);
                    final int mMonth = calendar.get(Calendar.MONTH);
                    final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                    final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int mMinute = calendar.get(Calendar.MINUTE);
                    switch (dateOrTime) {
                        case "date": {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    editText.setText(dayOfMonth + "/" + month + "/" + year);
                                }
                            }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                            break;
                        }
                        case "time": {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    if (hourOfDay > 12)
                                        editText.setText(hourOfDay - 12 + ":" + minute + " " + "PM");
                                    else if (hourOfDay == 12)
                                        editText.setText(hourOfDay + ":" + minute + " " + "PM");
                                    else
                                        editText.setText(hourOfDay + ":" + minute + " " + "AM");
                                }
                            }, mHour, mMinute, false);
                            timePickerDialog.show();
                            break;
                        }
                    }
                    return true;
                }
                return false;
            }
        };


        public String getEditText() {
            return editText.getText().toString();
        }

        public String getType() {
            return mSpinnerItems.get(type);
        }
    }
}
