package com.pinus.alexdev.avis.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.pinus.alexdev.avis.R;
import com.pinus.alexdev.avis.dto.response.BranchesResponse;
import com.pinus.alexdev.avis.utils.SaveLoadData;
import com.pinus.alexdev.avis.view.company_activity.AddBranchActivity;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

import static com.pinus.alexdev.avis.view.company_activity.AddBranchActivity.BRANCH_FOR_EDITING;

public class BranchAdapter extends ArrayAdapter<BranchesResponse> {
    private static final String TAG = BranchAdapter.class.getSimpleName();
    private final Context mContext;
    private FragmentManager fragmentManager;

    private CompositeDisposable disposable = new CompositeDisposable();

    private BranchListListener branchListListener;

    public void setBranchListListener(BranchListListener branchListListener) {
        this.branchListListener = branchListListener;
    }

    public interface BranchListListener {
        void openDeleteBranchDialog(int position);
    }

    public BranchAdapter(@NonNull Context context, @NonNull ArrayList<BranchesResponse> review, RxPermissions rxPermissions) {
        super(context, 0, review);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BranchesResponse branch = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.branch_card_item, parent, false);
        }

        String branchJson = new Gson().toJson(branch);

        Intent intent = new Intent(mContext, AddBranchActivity.class);
        intent.putExtra("branch", branchJson);
        intent.putExtra("checkBranch", BRANCH_FOR_EDITING);

        convertView.setOnClickListener(v -> mContext.startActivity(intent));

        CircleImageView avatarBranchImageInItem = convertView.findViewById(R.id.avatarBranchImageInItem);
        Picasso.get().load(Objects.requireNonNull(branch).getLogoUrl()).placeholder(R.drawable.avatar).error(R.drawable.avatar).into(avatarBranchImageInItem);

        //Этот код нужен был для того, чтобы обновлять фото в списке. Чтобы открыть галарею для добавления фото, должен(после открытия галереи и выбора фотки) вызываться системный метод onActivityResult(),
        //А поскольку этот метод вызывается только в актиити, то пришлость делать callBack, чтобы удалость открывать галерею и получать выбранное фото в список.
        //Обновление фото перешло в отдельное активити, этот код я оставил на всякий случай, если дизайн снова поменяется на прежний вариант
//        disposable.add(RxView.clicks(avatarBranchImageInItem).subscribe(t -> branchPhotoListener.updateBranchPhotoInItemCallBack(branch.getId())));

        TextView branchName = convertView.findViewById(R.id.branchNameTitle);
        TextView address = convertView.findViewById(R.id.addressCompanyTitle);
        TextView contact = convertView.findViewById(R.id.contactBranchTitle);
        TextView number = convertView.findViewById(R.id.numberCompanyTitle);

        branchName.setText(branch.getName());
        address.setText(branch.getAddress());
        contact.setText(branch.getContact());
        number.setText(branch.getPhone());

        ImageView btnDelete = convertView.findViewById(R.id.deleteBranchButtonI);
        btnDelete.setOnClickListener(v -> {
            SaveLoadData saveLoadData = new SaveLoadData(mContext);

            saveLoadData.saveString("branchNameForDeleting", branch.getName()); //Сохраняем имя бранча для того, чтобы потом его можно было отобразить в диалоге удаления бранча
            saveLoadData.saveInt("branchIdForDeleting", branch.getId()); //Сохраняем id бранча для того, чтобы потом с его помощью можно было удалить бранч

            branchListListener.openDeleteBranchDialog(position);
            // На всякий случай если понадобится создание обычного диалога
//            new AlertDialog.Builder(mContext)
//                    .setTitle(getContext().getString(R.string.delete))
//                    .setContent(getContext().getString(R.string.deleteBranchQ))
//                    .setIcon(R.drawable.ic_delete)
//                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
//                            ApiClient.getClient(mContext).create(BranchApiService.class).deleteBranch(Objects.requireNonNull(branch).getId(), new SaveLoadData(mContext).loadInt(ORGANIZATION_ID_KEY))
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(t -> {
//                                                LocaleHelper.onAttach(mContext, "en");
//                                                remove(branch);
//                                                StyleableToast.makeText(mContext, getContext().getString(R.string.branchDelete), Toast.LENGTH_LONG, R.style.mytoast).show();
//                                            },
//                                            e -> {
//                                                StyleableToast.makeText(mContext, getContext().getString(R.string.error), Toast.LENGTH_LONG, R.style.mytoast).show();
//                                                Log.e(TAG, "onError: " + e.getContent());
//                                            }
//                                    ))
//                    .setNegativeButton(android.R.string.no, null)
//                    .show();
        });

        return convertView;
    }

    public void remove(int position) {
        remove(getItem(position));
    }
}
