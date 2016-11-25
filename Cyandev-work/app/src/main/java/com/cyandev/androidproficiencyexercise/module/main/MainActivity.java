package com.cyandev.androidproficiencyexercise.module.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cyandev.androidproficiencyexercise.R;
import com.cyandev.androidproficiencyexercise.module.entity.EntityFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter presenter;

    private CategoriesPagerAdapter categoriesPagerAdapter;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.main_title);

        categoriesPagerAdapter = new CategoriesPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(categoriesPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        presenter.onAttachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MainContract.Presenter getPresenter() {
        return presenter;
    }

    public void setTabs(List<String> tabs) {
        categoriesPagerAdapter.setCategories(tabs);
    }

    public class CategoriesPagerAdapter extends FragmentPagerAdapter {

        private List<String> categories;

        public CategoriesPagerAdapter(FragmentManager fm) {
            super(fm);
            this.categories = new ArrayList<>();
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return EntityFragment.instantiate(categories.get(position));
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position);
        }

    }
}
