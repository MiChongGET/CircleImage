package cn.buildworld.circleimage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private List<ImageView> imageViewList;
    private ArrayList<View> pointViews;
    private LinearLayout points;
    private String[] contentDescs;
    private TextView textView;
    private int previousSelectedPosition = 0;
    private boolean isUIVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化布局
        initViews();

        //Model数据
        initData();

        //Controller控制器
        initAdapter();

        //开启轮询
        new Thread(){
            @Override
            public void run() {
                isUIVisible = true;
                while (isUIVisible){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //往下跳一位
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                        }
                    });

                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUIVisible  =false;
    }

    private void initViews() {
        viewPager  = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(this);//设置滚动监听
        points = (LinearLayout) findViewById(R.id.all_point);
        textView = (TextView) findViewById(R.id.tv_desc);


    }
    private void initAdapter() {

        points.getChildAt(0).setEnabled(true);
        textView.setText(contentDescs[0]);

        viewPager.setAdapter(new MyAdapter());

        int pos = Integer.MAX_VALUE /2 - (Integer.MAX_VALUE /2 % imageViewList.size());
        viewPager.setCurrentItem(pos);

    }

    private void initData() {

        //图片资源数组
        int[] imageResIds = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e};

        // 文本描述
        contentDescs = new String[]{
                "巩俐不低俗，我就不能低俗",
                "扑树又回来啦！再唱经典老歌引万人大合唱",
                "揭秘北京电影如何升级",
                "乐视网TV版大派送",
                "热血屌丝的反杀"
        };

        //初始化要展示的5个ImageView
        ImageView imageView;
        imageViewList = new ArrayList<ImageView>();

        pointViews = new ArrayList<>();
        View pointView;
        LinearLayout.LayoutParams layoutParams;

        for (int i = 0;i<imageResIds.length;i++){
            imageView = new ImageView(this);
            imageView.setBackgroundResource(imageResIds[i]);
            imageViewList.add(imageView);

            //加小白点，指示器
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.select_bg_points);

            layoutParams = new LinearLayout.LayoutParams(12, 12);
            if(i != 0) {
                layoutParams.leftMargin = 10;
            }

            pointView.setEnabled(false);

            points.addView(pointView,layoutParams);

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滚动调用
    }

    @Override
    public void onPageSelected(int position) {

        int newPosition = position % imageViewList.size();
        //新的条目被选中时调用
        textView.setText(contentDescs[newPosition]);

        points.getChildAt(previousSelectedPosition).setEnabled(false);
        points.getChildAt(newPosition).setEnabled(true);

        previousSelectedPosition = newPosition;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //滚动状态变化时调用
    }


    class MyAdapter extends PagerAdapter{



        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }


        //3、指定复用的判断逻辑，固定写法
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //当找到新的条目，又返回回来，view是否可以被复用
            //返回判断规则

            return view == object;
        }


        //1、返回要显示的内容，创建条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //container容器，viewpager
            //position：当前要显示的条目的位置

            int newPosition = position % imageViewList.size();
            //a.吧view对象添加到container中
            ImageView imageView = imageViewList.get(newPosition);
            container.addView(imageView);

            //b.把view对象返回给框架，适配器

            return imageView;//必须重写，否则异常
        }


        //2、返回要显示的内容，销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           //object 要销毁的对象

            container.removeView((View) object);
        }
    }
}
