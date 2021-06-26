package com.benjaminshamoilia.trackerapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.Constant;
import com.benjaminshamoilia.trackerapp.helper.Utility;
import com.benjaminshamoilia.trackerapp.interfaces.API;
import com.benjaminshamoilia.trackerapp.vo.NotificationData;
import com.benjaminshamoilia.trackerapp.vo.VoBluetoothDevices;
import com.benjaminshamoilia.trackerapp.vo.VoNotification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.*;

/**
 * Created by Jaydeep on 22-12-2017.
 */

public class FragmentNotification extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    @BindView(R.id.frg_notification_rv)
    RecyclerView mRecyclerView;

    Utility mUtility;

    @BindView(R.id.frg_notification_tv_no_notification)
    TextView mTextViewNoDeviceFound;

    @BindView(R.id.frg_notification_rl_no_notification)
    RelativeLayout mRelativeLayoutNoDevice;
    @BindView(R.id.frg_notification_progress)
    ProgressBar mProgressBar;
    ArrayList<VoBluetoothDevices> mArrayListAddDevice = new ArrayList<>();
    NotificationListAdapter mNotificationListAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.fragment_notification, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_notification);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.showBackButton(false);
        mUtility = new Utility(getActivity());
        mArrayListAddDevice = new ArrayList<>();
        mNotificationListAdapter = null;
        getDeviceListData();
        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });

        mProgressBar=new ProgressBar(getActivity());
        mProgressBar.setIndeterminate(false);
        Get_NotificationList();
        return mViewRoot;
    }

    /* Initialize device adapter*/
    private void getDeviceListData() {
        if (mNotificationListAdapter == null) {
            mNotificationListAdapter = new NotificationListAdapter();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mNotificationListAdapter);


            mNotificationListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkAdapterIsEmpty();
                }
            });
            checkAdapterIsEmpty();
        } else {
            mNotificationListAdapter.notifyDataSetChanged();
        }
        checkAdapterIsEmpty();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        unbinder.unbind();
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.mImageViewBack.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void checkAdapterIsEmpty() {
        if (isAdded()) {
            if (mNotificationListAdapter != null) {
                if (mNotificationListAdapter.getItemCount() > 0) {
                    mRelativeLayoutNoDevice.setVisibility(View.GONE);
                } else {
                    mRelativeLayoutNoDevice.setVisibility(View.VISIBLE);
                }
            } else {
                mRelativeLayoutNoDevice.setVisibility(View.VISIBLE);
            }
        }
    }

    /*Device list adapter*/
    public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_notification_list_item, parent, false);
            return new NotificationListAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            // holder.mTextViewNotificationName.setText(x.get(position).getNotification());
            // holder.time.setText(x.get(position).getCreated_at());
            String time = mUtility.convert_time(x.get(position).getCreated_at());
            // holder.time.setText(time);

            String CustomUrl = x.get(position).getNotification();
            String Remove_customLocation = CustomUrl.substring(CustomUrl.lastIndexOf("location") + 8);
            String device = CustomUrl.replace(Remove_customLocation, "");
            holder.mTextViewNotificationName.setText(device + "\n" + time);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdded()) {
                        // Toast.makeText(getActivity(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
//https://maps.google.com/maps?q=12.341206,76.615097
                        String url = x.get(position).getNotification();
                        final String Obtained_link = url.substring(url.lastIndexOf("location") + 8);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Uri gmmIntentUri = Uri.parse(Obtained_link);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        }, 1000);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return x != null ? x.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.raw_notification_tv_name)
            TextView mTextViewNotificationName;
            @BindView(R.id.raw_time)
            TextView time;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    List<NotificationData> x = new ArrayList<>();

    private void Get_NotificationList() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.MAIN_URL)
                .client(mActivity.mUtility.getClientWithAutho())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .build();
        API mApiService = mRetrofit.create(API.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mActivity.mPreferenceHelper.getUserId());
        params.put("limit", "20");
        params.put("offset", "0");

        final Call<VoNotification> notification_list = mApiService.Notification_List(params);
        notification_list.enqueue(new Callback<VoNotification>() {
            @Override
            public void onResponse(Call<VoNotification> call, Response<VoNotification> response) {

                VoNotification voNotification = response.body();
                if (voNotification.getMessage().startsWith("Invalid Token")) {
                    mActivity.APICrushLogout();
                } else
                    {
                        List<NotificationData> notificationData = voNotification.getData();
                        x.clear();
                        x.addAll(notificationData);
                        if (mNotificationListAdapter != null) {
                            mNotificationListAdapter.notifyDataSetChanged();
                        }
                        for (int i = 0; i < notificationData.size(); i++) {
                            System.out.println("Fragment_Notification = " + notificationData.get(i).getFromUserId());
                            System.out.println("Fragment_Notification = " + notificationData.get(i).getTo_user_id());
                            System.out.println("Fragment_Notification = " + notificationData.get(i).getNotification());
                            System.out.println("Fragment_Notification = " + notificationData.get(i).getCreated_at());
                            System.out.println("Fragment_Notification = " + notificationData.get(i).getId());
                        }
                    }




            }

            @Override
            public void onFailure(Call<VoNotification> call, Throwable t) {

            }
        });
    }

}
