package com.englandstudio.aloha;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.englandstudio.aloha.fragments.FriendsFragment;
import com.englandstudio.aloha.fragments.MessagesFragment;
import com.englandstudio.aloha.fragments.NewsFragment;
import com.englandstudio.aloha.fragments.NotificationsFragment;
import com.englandstudio.aloha.fragments.OptionsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NewsFragment newsFragment = new NewsFragment();
                return newsFragment;
            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
                return messagesFragment;
            case 2:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 3:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 4:
                OptionsFragment optionsFragment = new OptionsFragment();
                return optionsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
