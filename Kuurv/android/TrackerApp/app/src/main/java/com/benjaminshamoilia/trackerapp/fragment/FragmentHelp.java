package com.benjaminshamoilia.trackerapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.benjaminshamoilia.trackerapp.MainActivity;
import com.benjaminshamoilia.trackerapp.R;
import com.benjaminshamoilia.trackerapp.helper.ChildModel;
import com.benjaminshamoilia.trackerapp.helper.HeaderModel;
import com.benjaminshamoilia.trackerapp.helper.Section;
import com.benjaminshamoilia.trackerapp.helper.SectionAdapter;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;

import java.util.ArrayList;

public class FragmentHelp extends Fragment {
    MainActivity mActivity;
    View mViewRoot;
    private Unbinder unbinder;
    /* @BindView(R.id.frg_help_rv)
     RecyclerView mRecyclerView;*/
    // ArrayList<String> mArrayListHelp = new ArrayList<>();
    // HelpListAdapter mHelpListAdapter;
    // SwipeRe mSwipeRefreshLayout;
   // SwipeRefreshLayout swipeRefreshLayout ;


    @BindView(R.id.answer6)
    TextView connecteatKuurv;

    private String[] vehicleTypes = new String[]{
            "1.\tMy Bluetooth is on but I can’t Access my Kuurv Tracker",
            "2.\tHow to Place Your Kuurv Tracker on a Device",
            "3.\tHow to Physically Remove the Tracker off of a Device",
            "4.\tHow to delete your tracker",
            "5.\tHow to Re-Apply the Adhesive Tape to Your Tracker",
            "6.\tMore Help"
    };

    private ArrayList<Section> sectionArrayList;

    private String[] childnames = new String[]
            {
                    "Sometimes you may need to exit out of the app itself and then go back into in app to access your Kuurv tracker successfully.",
                    "a.\tMake sure surface of device being placed on is clean and dry (you may clean with rubbing alcohol, if needed)",
                    "b.\tGently peel off the blue adhesive peel off of the bottom of the tracker",
                    "c.\tGently place the tracker on your device (Make sure it is on the spot you want, and also be aware that the tracker is not obstructing any charging outlets)",
                    "d.\tNow press down firmly and on the top of the tracker and all sides of it (Do it for 30 seconds)",
                    "You may use rubbing alcohol and a dull butter knife to gently pry the tracker off of any device. Please be cautious when doing this.",
                    "a.\tSelect the Kuurv tracker you wish you delete (if there are multiples on your account)",
                    "b.\tPress the more options (3 dots icon) on the top right corner of the homepage",
                    "c.\tSelect Delete Tracker\n" +
                            "*Note Kuurv tracker must be connected in order to delete\n",
                    "a.\tUse rubbing alcohol and a cloth to clean the adhesive residue off of the bottom of the tracker",
                    "b.\tMake sure the bottom of the tracker is pretty clean and dry",
                    "c.\tUse the pull tab on the blue adhesive to remove it from its original display",
                    "d.\tCarefully place the adhesive on the bottom of the tracker fitting as best as possible",
                    "e.\tPress down firmly on all sides of the adhesive to make sure the adhesive transfers over onto the bottom of the tracker when you are ready to remove it",
                    "f.\tFirmly press down on the tape for 20-30 seconds",
                    "g.\tWhen you are ready to place the tracker on an item, remove the blue strip and apply ",
                    "Please reach out to connect@kuurvtracker.com and we will respond within 24 hours or sooner!"

            };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(R.layout.dummytesting, container, false);
        unbinder = ButterKnife.bind(this, mViewRoot);
        mActivity.mTextViewTitle.setText(R.string.str_title_help);
        mActivity.mTextViewTitle.setTextColor(Color.WHITE);
        mActivity.mTextViewTitle.setGravity(Gravity.CENTER);
        mActivity.mTextViewTitle.setTypeface(null, Typeface.BOLD);
        mActivity.mTextViewTitle.setTextSize(23);
        mActivity.mImageViewBack.setVisibility(View.GONE);
        mActivity.mImageViewAddDevice.setVisibility(View.GONE);
        mActivity.showBackButton(false);
        //     mArrayListHelp = new ArrayList<>();
        //   mArrayListHelp.add("How to delete Tracker?");
        //   mArrayListHelp.add("Can the same nut be used by another account after delete?");
        //   mArrayListHelp.add("How many trackers can be paired with one phone?");
        //   mArrayListHelp.add("User Manual");

        //    mHelpListAdapter = null;
        //  getDeviceListData();

      //  swipeRefreshLayout = (SwipeRefreshLayout)mViewRoot. findViewById(R.id.swipe_view);
       // RecyclerView recyclerView = mViewRoot.findViewById(R.id.recycler_view);

     //   recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sectionArrayList = new ArrayList<>();
        populateList();

        SectionAdapter adapter = new SectionAdapter(getActivity(),sectionArrayList);
    //    recyclerView.setAdapter(adapter);
        StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(adapter);
   //     decorator.attachToRecyclerView(recyclerView);



        mActivity.mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });


   /*     swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //shuffle();
                swipeRefreshLayout.setRefreshing(false);
                populateList();
            }
        });*/



        return mViewRoot;
    }

    /* Initialize device adapter*/
   /* private void getDeviceListData() {
        if (mHelpListAdapter == null) {
            mHelpListAdapter = new HelpListAdapter();
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mHelpListAdapter);
        } else {
            mHelpListAdapter.notifyDataSetChanged();
        }
    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mRecyclerView.setAdapter(null);
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

    /*    *//*Device list adapter*//*
    public class HelpListAdapter extends RecyclerView.Adapter<HelpListAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_help_list_item, parent, false);
            return new HelpListAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mTextViewTitle.setText(mArrayListHelp.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdded()) {
                        mActivity.replacesFragment(new FragmentHelpDetails(), true, null, 0);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArrayListHelp.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.raw_help_item_tv_name)
            TextView mTextViewTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }*/

    private void populateList(){

        int headerdone = 0, childdone = 0;

        for(int i = 0; i < 23; i++){

            if(i == 0 || i == 2 | i == 7 | i == 9|i==13|i==21){

                HeaderModel vehicleModel = new HeaderModel(i);
                vehicleModel.setheader(vehicleTypes[headerdone]);
                sectionArrayList.add(vehicleModel);
                headerdone = headerdone + 1;
            }else {

                ChildModel childModel = null;
                if(i == 1){
                    childModel = new ChildModel(0);//0
                }

                else if(i == 3 || i == 4 || i == 5 || i == 6)
                {
                    childModel = new ChildModel(2);//2
                }

                else if(i == 8){
                    childModel = new ChildModel(7);//7
                }
                else if(i == 10 || i == 11 || i == 12 )
                {
                    childModel = new ChildModel(9);//9
                }

                else if(i == 14 || i == 15 || i == 16|i == 17 || i == 18 || i == 19)
                {
                    childModel = new ChildModel(13);//13
                }

                else
                {
                    childModel = new ChildModel(21);
                }

                childModel.setChild(childnames[childdone]);
                sectionArrayList.add(childModel);
                childdone = childdone + 1;
            }
        }

    }


    @OnClick(R.id.answer6)
    public void connecttoKuurcclick(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"connect@kuurvtracker.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Help");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, ""));
    }


}


//http://androidsurya.blogspot.com/2012/11/android-pull-to-refresh-listview-example.html