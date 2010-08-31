package com.xtremelabs.droidsugar.fakes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import com.xtremelabs.droidsugar.ProxyDelegatingHandler;
import com.xtremelabs.droidsugar.util.Implements;

import java.lang.reflect.Constructor;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(AlertDialog.class)
public class FakeAlertDialog extends FakeDialog {
    public static FakeAlertDialog latestAlertDialog;

    public CharSequence[] items;
    public String title;
    public String message;
    private DialogInterface.OnClickListener clickListener;
    private AlertDialog realDialog;
    private boolean isMultiItem;
    private boolean isSingleItem;
    private DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener;
    public boolean[] checkedItems;
    private int checkedItemIndex;
    private Button positiveButton;
    private Button negativeButton;
    private Button neutralButton;
    private boolean isCancelable;

    public FakeAlertDialog(AlertDialog dialog) {
        super(dialog);
    }

    @Override
    public View findViewById(int viewId) {
        return null;
    }

    public void clickOnItem(int index) {
        if (isMultiItem) {
            checkedItems[index] = !checkedItems[index];
            multiChoiceClickListener.onClick(realDialog, index, checkedItems[index]);
        } else {
            if (isSingleItem) {
                checkedItemIndex = index;
            }
            clickListener.onClick(realDialog, index);
        }
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case AlertDialog.BUTTON_POSITIVE:
                return positiveButton;
            case AlertDialog.BUTTON_NEGATIVE:
                return negativeButton;
            case AlertDialog.BUTTON_NEUTRAL:
                return neutralButton;
        }
        throw new RuntimeException("huh?");
    }

    @Implements(AlertDialog.Builder.class)
    public static class FakeBuilder {
        private CharSequence[] items;
        private DialogInterface.OnClickListener clickListener;
        private String title;
        private String message;
        private AlertDialog.Builder realBuilder;
        private Context context;
        private boolean isMultiItem;
        private DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener;
        private boolean[] checkedItems;
        private CharSequence positiveText;
        private DialogInterface.OnClickListener positiveListener;
        private CharSequence negativeText;
        private DialogInterface.OnClickListener negativeListener;
        private CharSequence neutralText;
        private DialogInterface.OnClickListener neutralListener;
        private boolean isCancelable;
        private boolean isSingleItem;
        private int checkedItem;

        public FakeBuilder(AlertDialog.Builder realBuilder) {
            this.realBuilder = realBuilder;
        }

        public void __constructor__(Context context) {
            this.context = context;
        }

        public AlertDialog.Builder setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
            this.isMultiItem = false;

            this.items = items;
            this.clickListener = listener;
            return realBuilder;
        }

        public AlertDialog.Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final DialogInterface.OnClickListener listener) {
            this.isSingleItem = true;
            this.checkedItem = checkedItem;
            this.items = items;
            this.clickListener = listener;
            return realBuilder;
        }

        public AlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                                       final DialogInterface.OnMultiChoiceClickListener listener) {
            this.isMultiItem = true;

            this.items = items;
            this.multiChoiceClickListener = listener;

            if (checkedItems == null) {
                checkedItems = new boolean[items.length];
            } else if (checkedItems.length != items.length) {
                throw new IllegalArgumentException("checkedItems must be the same length as items, or pass null to specify checked items");
            }
            this.checkedItems = checkedItems;

            return realBuilder;
        }

        public AlertDialog.Builder setTitle(CharSequence title) {
            this.title = title.toString();
            return realBuilder;
        }

        public AlertDialog.Builder setTitle(int titleId) {
            this.title = context.getResources().getString(titleId);
            return realBuilder;
        }

        public AlertDialog.Builder setMessage(CharSequence message) {
            this.message = message.toString();
            return realBuilder;
        }

        public AlertDialog.Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            this.positiveText = text;
            this.positiveListener = listener;
            return realBuilder;
        }

        public AlertDialog.Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            this.negativeText = text;
            this.negativeListener = listener;
            return realBuilder;
        }

        public AlertDialog.Builder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            this.neutralText = text;
            this.neutralListener = listener;
            return realBuilder;
        }

        public AlertDialog.Builder setCancelable(boolean cancelable) {
            this.isCancelable = cancelable;
            return realBuilder;
        }

        public AlertDialog create() {
            AlertDialog realDialog;
            try {
                Constructor<AlertDialog> c = AlertDialog.class.getDeclaredConstructor(Context.class);
                c.setAccessible(true);
                realDialog = c.newInstance((Context) null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            FakeAlertDialog latestAlertDialog = proxyFor(realDialog);
            latestAlertDialog.context = context;
            latestAlertDialog.realDialog = realDialog;
            latestAlertDialog.items = items;
            latestAlertDialog.title = title;
            latestAlertDialog.message = message;
            latestAlertDialog.clickListener = clickListener;
            latestAlertDialog.isMultiItem = isMultiItem;
            latestAlertDialog.isSingleItem = isSingleItem;
            latestAlertDialog.checkedItemIndex = checkedItem;
            latestAlertDialog.multiChoiceClickListener = multiChoiceClickListener;
            latestAlertDialog.checkedItems = checkedItems;
            latestAlertDialog.positiveButton = createButton(realDialog, AlertDialog.BUTTON_POSITIVE, positiveText, positiveListener);
            latestAlertDialog.negativeButton = createButton(realDialog, AlertDialog.BUTTON_NEGATIVE, negativeText, negativeListener);
            latestAlertDialog.neutralButton = createButton(realDialog, AlertDialog.BUTTON_NEUTRAL, neutralText, neutralListener);
            latestAlertDialog.isCancelable = isCancelable;

            FakeAlertDialog.latestAlertDialog = latestAlertDialog;

            return realDialog;
        }

        public AlertDialog show() {
            AlertDialog dialog = realBuilder.create();
            dialog.show();
            return dialog;
        }

        private Button createButton(final DialogInterface dialog, final int which, CharSequence text, final DialogInterface.OnClickListener listener) {
            Button button = new Button(context);
            button.setText(text);
            button.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onClick(dialog, which);
                }
            });
            return button;
        }
    }

    private static FakeAlertDialog proxyFor(AlertDialog realDialog) {
        return (FakeAlertDialog) ProxyDelegatingHandler.getInstance().proxyFor(realDialog);
    }
}
