package construction.thesquare.shared.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import construction.thesquare.R;
import construction.thesquare.shared.view.widget.LoadingDialog;
import construction.thesquare.worker.onboarding.OnLanguagesSelectedListener;

public class DialogBuilder {

    public interface OnClickStandardDialog {
        void onOKClickStandardDialog(Context context);
    }

    public interface OnClickTwoOptionsStandardDialog {
        void onClickOptionOneStandardDialog(Context context);

        void onClickOptionTwoStandardDialog(Context context);
    }

    public interface OnTextInputDialogListener {
        void onInputFinished(String input);
    }

    public static Dialog showCustomDialog(Context context) {
        if (Build.VERSION.SDK_INT > 14) {
            Dialog dialog = new LoadingDialog(context);
            dialog.show();
            if (dialog.getWindow() != null) {
                dialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            return dialog;
        } else {
            Dialog dialog = ProgressDialog.show(context, null, null);
            dialog.setContentView(R.layout.loader);
            if (dialog.getWindow() != null) {
                dialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            return dialog;
        }
    }

    public static void cancelDialog(Dialog dialog) {
        try {
            if (dialog != null) {
                dialog.cancel();
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    public static void showStandardDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        afterShowSetProperties(builder, context);
        //builder.show();
    }

    public static void showStandardDialog(final Context context, String title, String message,
                                          final OnClickStandardDialog listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onOKClickStandardDialog(context);
            }
        });
        afterShowSetProperties(builder, context);
        //builder.show();
    }

    public static void showStandardDialog(final Context context, String title, String message,
                                          final DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.d("tag", String.valueOf(dialog.hashCode()));

                        dialog.dismiss();
                        listener.onClick(dialog, id);
                    }
                });
        afterShowSetProperties(builder, context);
        //builder.show();
    }

    public static Dialog showTwoOptionsStandardDialog(final Context context, String title, String message,
                                                      String btnOneText, String btnTwoText, final OnClickTwoOptionsStandardDialog listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message).setPositiveButton(btnTwoText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onClickOptionTwoStandardDialog(context);
            }
        });
        builder.setMessage(message).setNegativeButton(btnOneText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                listener.onClickOptionOneStandardDialog(context);
            }
        });
        afterShowSetProperties(builder, context);
        return builder.create();
    }

    public static void showDeleteDraftDialog(final Context context,
                                             final View.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // TODO: move strings out into resources
        builder.setTitle("Delete Draft");
        builder.setMessage("Are you sure you want to delete this draft job?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onClick(new View(context));
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    public static AlertDialog.Builder getStandardDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        return builder;
    }

    public static void showInputDialog(final Context context,
                                       @StringRes int titleResId,
                                       @StringRes int hintResId,
                                       final OnTextInputDialogListener listener) {
        if (context == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_with_input, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        TextInputLayout textInputLayout = (TextInputLayout) viewInflated.findViewById(R.id.inputLayout);
        builder.setView(viewInflated);

        if (titleResId > 0) builder.setTitle(titleResId);
        if (hintResId > 0) textInputLayout.setHint(context.getString(hintResId));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) listener.onInputFinished(input.getText().toString());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        afterShowSetProperties(builder, context);
    }

    public static void showCancelBookingDialog(Context context, DialogInterface.OnClickListener listener) {
        if (context == null) return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_booking_cancel, null, false);
        builder.setView(viewInflated);

        viewInflated.findViewById(R.id.dialog_booking_keep).setVisibility(View.GONE);
        viewInflated.findViewById(R.id.dialog_booking_cancel).setVisibility(View.GONE);

        builder.setNegativeButton(R.string.employer_dialog_booking_cancel, listener);

        builder.setPositiveButton(R.string.job_i_can_make_it, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        afterShowSetProperties(builder, context);
    }

    public static void afterShowSetProperties(AlertDialog.Builder builder, Context context) {

        try {
            if (context == null) return;
            Typeface typeFaceSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
            Typeface typeFaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/JosefinSans-Bold.ttf");
            AlertDialog alert = builder.create();
            alert.show();
            TextView tvMessage = (TextView) alert.findViewById(android.R.id.message);
            if (tvMessage != null) tvMessage.setTypeface(typeFaceSemiBold);

            TextView tvTitle = (TextView) alert.findViewById(android.R.id.title);
            if (tvTitle != null)
                tvTitle.setTypeface(typeFaceBold);

            Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.redSquareColor));
            positiveButton.setTypeface(typeFaceBold);

            Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (negativeButton != null) {
                negativeButton.setTextColor(ContextCompat.getColor(context, R.color.redSquareColor));
                negativeButton.setTypeface(typeFaceBold);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    public static void showMultiSelectDialog(Context context, CharSequence[] dialogList,
                                             final OnLanguagesSelectedListener listener) {
        if (context == null) return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        int count = dialogList.length;
        boolean[] isChecked = new boolean[count];

        builder.setMultiChoiceItems(dialogList, isChecked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView list = ((AlertDialog) dialog).getListView();
                        List<String> result = new ArrayList<>();
                        for (int i = 0; i < list.getCount(); i++) {

                            if (list.isItemChecked(i)) {
                                result.add((String) list.getItemAtPosition(i));
                            }
                        }
                        if (listener != null)
                            listener.onLanguagesSelected(result);
                    }
                }

        );

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );
        AlertDialog alert = builder.create();
        alert.show();
        //return selectedLanguages.toString();
    }
}