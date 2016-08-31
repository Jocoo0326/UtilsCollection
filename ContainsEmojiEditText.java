package com.yxtk.views;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 限制输入表情的EditText
 * 出自https://github.com/OrzMichael/ContainsEmojiEditText
 * 修复多选文字替换崩溃的bug
 *
 * Created by Jocoo on 2016/8/31.
 */
public class ContainsEmojiEditText extends EditText {
    //输入表情前的光标开始位置
    private int cursorStart;

    //输入表情前的光标结束位置
    private int cursorEnd;

    //输入表情前EditText中的文本
    private String inputAfterText;

    //是否重置了EditText的内容
    private boolean resetText;

    private Context mContext;

    public ContainsEmojiEditText(Context context) {
        this(context, null);
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    cursorStart = start;
                    cursorEnd = getSelectionEnd();
                    //这里用s.toString()而不直接用s是因为如果用s，
                    // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                    // inputAfterText也就改变了，那么表情过滤就失败了
                    inputAfterText = s.toString();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count >= 2) {//表情符号的字符长度最小为2
                        CharSequence input = s.subSequence(cursorStart, cursorStart + count);
                        if (containsEmoji(input.toString())) {
                            resetText = true;
                            //是表情符号就将文本还原为输入表情符号之前的内容
                            setText(inputAfterText);
                            CharSequence text = getText();
                            if (text != null) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, cursorEnd);
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000));
    }

}

