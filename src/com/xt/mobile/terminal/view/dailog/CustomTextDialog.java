package com.xt.mobile.terminal.view.dailog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xt.mobile.terminal.R;


/**
 * @Author:pengyongshun
 * @Desc:
 * @Time:2017/5/18
 */
public class CustomTextDialog extends Dialog
{

    public CustomTextDialog(Context context, int theme)
    {
        super(context, theme);
    }

    public CustomTextDialog(Context context)
    {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder
    {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;
        private Bitmap bitmap;
        private int imageId;
        private View layout;

        public Builder(Context context)
        {
            this.context = context;

        }

        /**
         * Set the Dialog message from String
         *
         * @param message
         * @return
         */
        public Builder setMessage(String message)
        {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message)
        {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title)
        {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title)
        {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         *
         * @param v
         * @return
         */
        public Builder setContentView(View v)
        {
            this.contentView = v;
            return this;
        }

        /**
         * Set the Dialog icon from int
         *
         * @param imageId
         * @return
         */
        public Builder setIcon(int imageId)
        {
            this.imageId = imageId;
            return this;
        }

        /**
         * Set the Dialog icon from Bitmap
         *
         * @param bitmap
         * @return
         */
        public Builder setIcon(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener)
        {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public void setPositiveButton(String positiveButtonText,
                                         OnClickListener listener)
        {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
//            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener)
        {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener)
        {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public CustomTextDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomTextDialog dialog = new CustomTextDialog(context,
                    R.style.MyDialogStyleBottom);
            //final CustomDialog dialog = new CustomDialog(context);
             layout = inflater.inflate(R.layout.cus_adialog_text_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null)
            {
                ((TextView) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null)
                {
                    ((TextView) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener()
                            {
                                public void onClick(View v)
                                {
                                    positiveButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                    if (dialog.isShowing())
                                    {
                                        dialog.dismiss();
                                    }
                                }
                            });
                } else
                {
                    ((TextView) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener()
                            {

                                @Override
                                public void onClick(View v)
                                {
                                    if (dialog.isShowing())
                                    {
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            } else
            {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null)
            {
                TextView viewById = (TextView) layout.findViewById(R.id.negativeButton);
                viewById.setText(negativeButtonText);
                //viewById.setTextColor(context.getResources().getColor(R.color.clock_color));
                if (negativeButtonClickListener != null)
                {
                    ((TextView) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener()
                            {
                                public void onClick(View v)
                                {
                                    negativeButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                    if (dialog.isShowing())
                                    {
                                        dialog.dismiss();
                                    }

                                }
                            });
                } else {
                    ((TextView) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener()
                            {

                                @Override
                                public void onClick(View v)
                                {
                                    if (dialog.isShowing())
                                    {
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            } else
            {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            //set the button line
            if (negativeButtonText == null && positiveButtonText == null)
            {
                layout.findViewById(R.id.button_line).setVisibility(View.GONE);
            } else
            {
                layout.findViewById(R.id.button_line).setVisibility(View.VISIBLE);
            }
            // set the content message
            if (message != null)
            {
                ((TextView) layout.findViewById(
                        R.id.message)).setText(message);
            } else if (contentView != null)
            {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView,
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            //set the content line
            if (message == null && contentView == null) {
                layout.findViewById(R.id.content_line).setVisibility(View.GONE);
            } else {
                layout.findViewById(R.id.content_line).setVisibility(View.VISIBLE);
            }
            return dialog;
        }


    }
}
