package com.example.s213463695.brew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Created by s213463695 on 2016/06/27.
 */
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.s213463695.brew.Home.main;
import static com.example.s213463695.brew.Home.username;
import static com.example.s213463695.brew.Home.email;
import static com.example.s213463695.brew.Home.cell;
import static com.example.s213463695.brew.Home.phone;
import static com.example.s213463695.brew.Home.profilePicture;
import static com.example.s213463695.brew.Home.profileExpanded;
import static com.example.s213463695.brew.Home.profilePictureFrame;

public class Profile extends Fragment {

    private ProfileListener mListener;
    public static ImageView profile_picture;
    public static Bitmap uploadImage = null;
    private Bitmap edit;
    private Bitmap tick;
    private AppCompatButton save;
    private boolean edit1_open = false;
    private boolean edit2_open = false;
    private boolean edit3_open = false;
    private boolean edit4_open = false;
    private TextView user_name;
    private TextView user_email;
    private TextView user_cell;
    private TextView user_phone;

    public Profile() {
    }

    public String getUser_name() {
        return user_name.getText().toString();
    }

    public String getUser_email() {
        return user_email.getText().toString();
    }

    public void setUser_name(String name) {
        user_name.setText(name);
    }

    public void setUser_email(String email) {
        user_email.setText(email);
    }

    public static Profile newInstance() {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity_profile, container, false);

        mListener.LoadAdditionalInfo();

        edit = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_mode_edit_black_24dp);
        tick = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_check_black_24dp);

        RelativeLayout body1 = (RelativeLayout) v.findViewById(R.id.body1);
        RelativeLayout body2 = (RelativeLayout) v.findViewById(R.id.body2);
        RelativeLayout body3 = (RelativeLayout) v.findViewById(R.id.body3);
        RelativeLayout body4 = (RelativeLayout) v.findViewById(R.id.body4);

        final ImageView edit1 = (ImageView) v.findViewById(R.id.edit1);
        final ImageView edit2 = (ImageView) v.findViewById(R.id.edit2);
        final ImageView edit3 = (ImageView) v.findViewById(R.id.edit3);
        final ImageView edit4 = (ImageView) v.findViewById(R.id.edit4);

        user_cell = (TextView) v.findViewById(R.id.cell_profile);
        user_phone = (TextView) v.findViewById(R.id.phone_profile);
        user_name = (TextView) v.findViewById(R.id.username_profile);
        user_email = (TextView) v.findViewById(R.id.email_profile);

        user_cell.setText(cell);
        user_phone.setText(phone);
        user_name.setText(username);
        user_email.setText(email);

        final ViewSwitcher switcherU = (ViewSwitcher) v.findViewById(R.id.username_switcher);
        final ViewSwitcher switcherE = (ViewSwitcher) v.findViewById(R.id.email_switcher);
        final ViewSwitcher switcherC = (ViewSwitcher) v.findViewById(R.id.cell_switcher);
        final ViewSwitcher switcherP = (ViewSwitcher) v.findViewById(R.id.phone_switcher);

        final EditText editPhone = (EditText) v.findViewById(R.id.profile_phone_edit);
        final EditText editCell = (EditText) v.findViewById(R.id.profile_cell_edit);
        final EditText editEmail = (EditText) v.findViewById(R.id.profile_email_edit);
        final EditText editUsername = (EditText) v.findViewById(R.id.profile_username_edit);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPictureIntent(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });

        save = (AppCompatButton) v.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
                Snackbar.make(v, "Confirm by clicking on the right -->", Snackbar.LENGTH_LONG)
                        .setAction("Save", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notifyChanges(user_name.getText().toString(), user_email.getText().toString(), user_cell.getText().toString(), user_phone.getText().toString());
                            }
                        }).show();
            }
        });

        AppBarLayout header = (AppBarLayout) v.findViewById(R.id.app_bar);
        profile_picture = (ImageView) v.findViewById(R.id.profile_picture);
        profile_picture.setImageBitmap(profileExpanded);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPictureIntent(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });

        body1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });
        body2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });
        body3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });
        body4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            }
        });

        editUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager in = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editUsername.getWindowToken(), 0);

                    closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
                    return true;
                } else
                    return false;
            }
        });

        editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager in = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);

                    closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
                    return true;
                } else
                    return false;
            }
        });

        editCell.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager in = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editCell.getWindowToken(), 0);

                    closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
                    return true;
                } else
                    return false;
            }
        });

        editPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    InputMethodManager in = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editPhone.getWindowToken(), 0);

                    closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
                    return true;
                } else
                    return false;
            }
        });

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit1_open) {
                    edit1.setImageBitmap(edit);
                    switcherU.showNext();
                    user_name.setText(editUsername.getText());
                    edit1_open = !edit1_open;
                } else {
                    edit1.setImageBitmap(tick);
                    switcherU.showNext();
                    editUsername.setText(user_name.getText());
                    edit1_open = !edit1_open;
                }
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, edit1, editUsername, editEmail, editCell, editPhone);
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit2_open) {
                    edit2.setImageBitmap(edit);
                    switcherE.showNext();
                    user_email.setText(editEmail.getText());
                    edit2_open = !edit2_open;
                } else {
                    edit2.setImageBitmap(tick);
                    switcherE.showNext();
                    editEmail.setText(user_email.getText());
                    edit2_open = !edit2_open;
                }
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, edit2, editUsername, editEmail, editCell, editPhone);
            }
        });
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit3_open) {
                    edit3.setImageBitmap(edit);
                    switcherC.showNext();
                    user_cell.setText(editCell.getText());
                    edit3_open = !edit3_open;
                } else {
                    edit3.setImageBitmap(tick);
                    switcherC.showNext();
                    editCell.setText(user_cell.getText());
                    edit3_open = !edit3_open;
                }
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, edit3, editUsername, editEmail, editCell, editPhone);
            }
        });
        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit4_open) {
                    edit4.setImageBitmap(edit);
                    switcherP.showNext();
                    user_phone.setText(editPhone.getText());
                    edit4_open = !edit4_open;
                } else {
                    edit4.setImageBitmap(tick);
                    switcherP.showNext();
                    editPhone.setText(user_phone.getText());
                    edit4_open = !edit4_open;
                }
                closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, edit4, editUsername, editEmail, editCell, editPhone);
            }
        });

        return v;
    }

    private void startPictureIntent(ViewSwitcher switcherC, ViewSwitcher switcherE, ViewSwitcher switcherP, ViewSwitcher switcherU,
                                    ImageView edit1, ImageView edit2, ImageView edit3, ImageView edit4, Object o, EditText editUsername, EditText editEmail, EditText editCell, EditText editPhone) {
        try {
            closeOpenInstances(switcherC, switcherE, switcherP, switcherU, edit1, edit2, edit3, edit4, null, editUsername, editEmail, editCell, editPhone);
            Intent in = getPickImageIntent(main);
            startActivityForResult(in, 0);
        } catch (Exception e) {
        }
    }

    private void closeOpenInstances(ViewSwitcher switcherC, ViewSwitcher switcherE, ViewSwitcher switcherP, ViewSwitcher switcherU,
                                    ImageView edit1, ImageView edit2, ImageView edit3, ImageView edit4, ImageView exception,
                                    EditText name, EditText email, EditText cell, EditText phone) {
        if (edit1_open && edit1 != exception) {
            edit1.setImageBitmap(edit);
            switcherU.showNext();
            user_name.setText(name.getText());
            edit1_open = !edit1_open;
        }
        if (edit2_open && edit2 != exception) {
            edit2.setImageBitmap(edit);
            switcherE.showNext();
            user_email.setText(email.getText());
            edit2_open = !edit2_open;
        }
        if (edit3_open && edit3 != exception) {
            edit3.setImageBitmap(edit);
            switcherC.showNext();
            user_cell.setText(cell.getText());
            edit3_open = !edit3_open;
        }
        if (edit4_open && edit4 != exception) {
            edit4.setImageBitmap(edit);
            switcherP.showNext();
            user_phone.setText(phone.getText());
            edit4_open = !edit4_open;
        }
    }

    private void notifyChanges(String name, String email, String cell, String phone) {
        mListener.triggerChanges(name, email, cell, phone);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMainListener(ProfileListener home) {
        this.mListener = home;
    }

    public interface ProfileListener {
        void triggerChanges(String name, String email, String cell, String phone);

        void saveProfilePicture(String encoded);

        void LoadAdditionalInfo();
    }

    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, (byte[]) null);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        intentList = addIntentsToList(context, intentList, pickIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), null);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }
}
