package com.pinus.alexdev.avis.view.company_activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dagang.library.GradientButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;
import com.pinus.alexdev.avis.Helper.LocaleHelper;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.adapter.BranchQrRVAdapter;
import com.pinus.alexdev.avis.dto.request.BranchRequest;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.QrManagerListResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.cta_response.CTAResponse;
import com.pinus.alexdev.avis.dto.response.qr_manager_list_response.opinion_response.OpinionResponse;
import com.pinus.alexdev.avis.model.CtaModel;
import com.pinus.alexdev.avis.model.OpinionModel;
import com.pinus.alexdev.avis.model.QrModel;
import com.pinus.alexdev.avis.network.ApiClient;
import com.pinus.alexdev.avis.network.apiServices.BranchApiService;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.BaseToolbarBack;
import com.pinus.alexdev.avis.view.PhotoCropActivity;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.pinus.alexdev.avis.utils.UserDataPref.userRequest;
import static com.pinus.alexdev.avis.utils.Utils.checkField;
import static com.pinus.alexdev.avis.view.LoginActivity.ORGANIZATION_ID_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_ACTIVITY_NAME_KEY;
import static com.pinus.alexdev.avis.view.PhotoCropActivity.INTENT_URI_KEY;

public class AddBranchActivity extends BaseToolbarBack implements BranchQrRVAdapter.SaveImageListener, DeleteBranchDialog.DeleteBranchDialogListener {
    private static final String TAG = AddBranchActivity.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();

    public static final int BRANCH_FOR_CREATING = 1;
    public static final int BRANCH_FOR_EDITING = 2;

    @BindView(R.id.appbar_back)
    View appbar;

    @BindView(R.id.backLayout)
    LinearLayout backTo;

    @BindView(R.id.avatarBranchImage)
    CircleImageView avatarBranchImage;

    @BindView(R.id.branchNameText)
    TextInputEditText branchNameText;

    @BindView(R.id.addressBranchText)
    TextInputEditText addressBranchText;

    @BindView(R.id.contactBranchText)
    TextInputEditText contactBranchText;

    @BindView(R.id.phoneBranchText)
    TextInputEditText phoneBranchText;

    @BindView(R.id.btnSave)
    GradientButton btnSave;

    @BindView(R.id.view)
    View view;

    @BindView(R.id.tvBranch)
    TextView tvBranch;

    @BindView(R.id.btnDeleteBranch)
    ImageButton btnDeleteBranch;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.branchQrsLayout)
    LinearLayout branchQrsLayout;

    DeleteBranchDialog deleteBranchDialog;

    private BranchQrRVAdapter branchQrRVAdapter;

    Intent intent;
    BranchesResponse branch = null;

    BranchApiService branchApiService;
    MultipartBody.Part branchPhoto;
    File file;

    public static Bitmap updatedPhotoBitmap;

    //Переменная предназначена для определения того, какой запрос нам нужно делать - для добавления фото при создании бранча или же для добавлении фото при редактировании бранча
    public boolean isPhotoForUpdate;

    private RxPermissions rxPermissions;

    //Класс для удобной работы с SharedPreferences
    SaveLoadData saveLoadData;

    private BranchesResponse finalBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_branch);
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);

        branchApiService = ApiClient.getClient(getApplicationContext()).create(BranchApiService.class);
        saveLoadData = new SaveLoadData(this);

        intent = getIntent();
        int checkBranch = intent.getIntExtra("checkBranch", -1);

        Picasso.get().load(R.drawable.avatar).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(avatarBranchImage);

        if (checkBranch == BRANCH_FOR_CREATING) {
            isPhotoForUpdate = false;
            setButtonBackClick(
                    appbar.findViewById(R.id.appBarTitle),
                    getResources().getString(R.string.addBranchButtonG),
                    appbar.findViewById(R.id.logoImageBack),
                    backTo,
                    appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                    "",
                    null
            );

            tvBranch.setVisibility(View.GONE);

            btnDeleteBranch.setVisibility(View.INVISIBLE);

            btnSave.getButton().setOnClickListener(v -> {
                if (checkField(branchNameText) && checkField(addressBranchText) && checkField(contactBranchText) && checkField(phoneBranchText)) {
                    saveBranchData(new BranchRequest(
                            Objects.requireNonNull(addressBranchText.getText()).toString(),
                            Objects.requireNonNull(contactBranchText.getText()).toString(),
                            Objects.requireNonNull(branchNameText.getText()).toString(),
                            Objects.requireNonNull(phoneBranchText.getText()).toString()
                    ));
                } else
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.fillAllFields), Toast.LENGTH_LONG, R.style.mytoast).show();
            });

        } else if (checkBranch == BRANCH_FOR_EDITING) {
            isPhotoForUpdate = true;
            setButtonBackClick(
                    appbar.findViewById(R.id.appBarTitle),
                    getResources().getString(R.string.editBranch),
                    appbar.findViewById(R.id.logoImageBack),
                    backTo,
                    appbar.findViewById(R.id.toolbarPreviosActivityTitle),
                    "",
                    null
            );

            branch = new Gson().fromJson(intent.getStringExtra("branch"), BranchesResponse.class);
            finalBranch = branch;

            Picasso.get().load(Objects.requireNonNull(branch).getLogoUrl()).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(avatarBranchImage);

            branchNameText.setText(branch.getName());
            addressBranchText.setText(branch.getAddress());
            contactBranchText.setText(branch.getContact());
            phoneBranchText.setText(branch.getPhone());

            btnDeleteBranch.setOnClickListener(v -> {
                saveLoadData.saveString("branchNameForDeleting", branch.getName()); //Сохраняем имя бранча для того, чтобы потом его можно было отобразить в диалоге удаления бранча
                saveLoadData.saveInt("branchIdForDeleting", branch.getId()); //Сохраняем id бранча для того, чтобы потом с его помощью можно было удалить бранч

                deleteBranchDialog = new DeleteBranchDialog();
                deleteBranchDialog.show(getSupportFragmentManager(), "delete branch dialog");

//                new AlertDialog.Builder(this)
//                        .setTitle(getString(R.string.delete))
//                        .setContent(getString(R.string.deleteBranchQ))
//                        .setIcon(R.drawable.ic_delete)
//                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
//                                disposable.add(ApiClient.getClient(this).create(BranchApiService.class).deleteBranch(finalBranch.getId(), saveLoadData.loadInt(ORGANIZATION_ID_KEY))
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(t -> {
//                                                    LocaleHelper.onAttach(this, "en");
//                                                    StyleableToast.makeText(this, getString(R.string.branchDelete), Toast.LENGTH_LONG, R.style.mytoast).show();
//                                                    startActivity(new Intent(this, CompanyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                                                    finish();
//                                                },
//                                                e -> {
//                                                    StyleableToast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
//                                                    Log.e(TAG, "onError: " + e.getContent());
//                                                }
//                                        )))
//                        .setNegativeButton(android.R.string.no, null).show();
            });

            btnSave.getButton().setOnClickListener(v -> {
                if (checkField(branchNameText) && checkField(addressBranchText) && checkField(contactBranchText) && checkField(phoneBranchText)) {
                    editBranchData(new BranchRequest(
                            Objects.requireNonNull(addressBranchText.getText()).toString(),
                            Objects.requireNonNull(contactBranchText.getText()).toString(),
                            Objects.requireNonNull(branchNameText.getText()).toString(),
                            Objects.requireNonNull(phoneBranchText.getText()).toString()
                    ), finalBranch.getId());
                } else
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.fillAllFields), Toast.LENGTH_LONG, R.style.mytoast).show();
            });

            branchQrsLayout.setVisibility(View.VISIBLE);
            getQrList(finalBranch.getId());
        }

        avatarBranchImage.setOnClickListener(v -> prepareAndUploadPhoto());
    }

    @SuppressLint("CheckResult")
    private void getQrList(long branchId) {
        disposable.add(branchApiService.getBranchQrsList(branchId, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setQrManagerContent,
                        e -> Log.e(TAG, "onError: " + e.getMessage())
                ));
    }

    private void setQrManagerContent(QrManagerListResponse qrList) {
        ArrayList<QrModel> qrModels = new ArrayList<>();
        for (CTAResponse response : qrList.getCta()) {
            CtaModel model = new CtaModel();
            model.setBranchId(response.getBranchId());
            model.setDefaultLocale(response.getDefaultLocale());
            model.setName(response.getName());
            model.setId(response.getId());
            model.setMessage(response.getMessage());
            model.setOptions(response.getOptions());
            model.setQuestion(response.getQuestion());
            model.setQrCode(response.getQrCode());
            qrModels.add(model);
        }

        for (OpinionResponse response : qrList.getOpinion()) {
            OpinionModel model = new OpinionModel();
            model.setBranchId(response.getBranchId());
            model.setDefaultLocale(response.getDefaultLocale());
            model.setName(response.getName());
            model.setId(response.getId());
            model.setMessage(response.getMessage());
            model.setAskNps(response.isAskNps());
            model.setQuestion(response.getQuestion());
            model.setQrCode(response.getQrCode());
            model.setCategory(response.getCategory());
            qrModels.add(model);
        }

        branchQrRVAdapter = new BranchQrRVAdapter(this, qrModels);
        branchQrRVAdapter.setSaveImageListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(branchQrRVAdapter);
        branchQrRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void downloadImage(String imageUrl, String imageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            disposable.add(rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            loadAndSaveImage(imageUrl, imageName);
                        } else {
                            StyleableToast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                            // Oups permission denied
                        }
                    }, e -> Log.e("BranchQrRVAdapter", "onError: " + e.getMessage())));
        } else {
            loadAndSaveImage(imageUrl, imageName);
        }
    }

    private void loadAndSaveImage(String imageUrl, String imageName) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToGallery(resource, imageName);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private String saveImageToGallery(Bitmap resource, String imageName) {
        String savedImagePath = null;

        String imageFileName = imageName + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/folder");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            StyleableToast.makeText(this, getString(R.string.image_saved), Toast.LENGTH_LONG, R.style.mytoast).show();
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
    private void saveBranchData(BranchRequest branchRequest) {
        disposable.add(branchApiService.addBranch(branchRequest, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (updatedPhotoBitmap != null) {
                                uploadBranchPhoto(updatedPhotoBitmap, response.getId());
                            }
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.branchAdd), Toast.LENGTH_LONG, R.style.mytoast).show();
                            startActivity(new Intent(this, CompanyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }
                ));
    }

    private void editBranchData(BranchRequest branchRequest, int branchId) {
        disposable.add(branchApiService.updateBranchInfo(branchRequest, branchId, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        t -> {
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.branchSaved), Toast.LENGTH_LONG, R.style.mytoast).show();
                            startActivity(new Intent(this, CompanyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.invalidPromo), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }
                ));
    }

    private void prepareAndUploadPhoto() {
        disposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    // granted - always true in pre-M
                    if (granted) getIntentImage();
                    else
                        StyleableToast.makeText(getApplicationContext(), "You don't have permission", Toast.LENGTH_LONG, R.style.mytoast).show();
                }));
    }

    private void getIntentImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK)
                .setType("image/*"), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, PhotoCropActivity.class);
            intent.putExtra(INTENT_URI_KEY, imageReturnedIntent.getData().toString());
            intent.putExtra(INTENT_ACTIVITY_NAME_KEY, "AddBranchActivity");
            startActivity(intent);
        }
    }

    private void uploadBranchPhoto(Bitmap bitmap, int branchId) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

        //Код для конвертации Bitmap в byteArray
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        resizedBitmap.recycle();

        RequestBody fbody = RequestBody.create(MediaType.parse("image/png"), byteArray);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "fileName", fbody);

        Log.v("ContentTag", fbody.contentType().toString());
        try {
            Log.v("ContentTag", String.valueOf(fbody.contentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateBranchPhoto(branchId, fbody, body);
    }

    private void updateBranchPhoto(int branchId, RequestBody fbody, MultipartBody.Part body) {
        disposable.add(branchApiService.updateBranchPhoto(branchId, fbody, body, saveLoadData.loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                            Log.v(TAG, "Image success");
                            //Эта проверка нужна для того, чтобы переходить на CompanyActivity в том случае, если мы добавляем бранч(чтобы список бранчей обновился при перезапуске активити).
                            //А также проверка нужна для того, чтобы включать и выключать Toast когда мы обновляем фото бранча или добавляем по новой
                            if (!isPhotoForUpdate) {
                                startActivity(new Intent(this, CompanyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else
                                StyleableToast.makeText(getApplicationContext(), getString(R.string.photoUpload), Toast.LENGTH_LONG, R.style.mytoast).show();
                            updatedPhotoBitmap = null;
                            userRequest(getApplicationContext());
                        },
                        e -> {
                            Log.e(TAG, "onError: " + e.getMessage());
                            StyleableToast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
                        }));
    }

    @Override
    public void deleteBranch(int branchId) {
        branchApiService
                .deleteBranch(branchId, new SaveLoadData(this).loadInt(ORGANIZATION_ID_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    StyleableToast.makeText(getApplicationContext(), getString(R.string.branch_deleted), Toast.LENGTH_LONG, R.style.mytoast).show();
                    deleteBranchDialog.dismiss(); // - закрытие диалога после удаления компании
                    startActivity(new Intent(this, CompanyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }, e -> Log.e(TAG, "onError:" + e.getMessage()));
    }

    @Override
    public void dismissDialog() {
        deleteBranchDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updatedPhotoBitmap != null) {
            avatarBranchImage.setImageBitmap(updatedPhotoBitmap);
            if (isPhotoForUpdate) {
                uploadBranchPhoto(updatedPhotoBitmap, finalBranch.getId());
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
