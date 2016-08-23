package com.wzy.helptravel.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.wzy.helptravel.R;
import com.wzy.helptravel.base.BaseToolBarActivity;
import com.wzy.helptravel.bean.CityModel;
import com.wzy.helptravel.bean.ProvinceModel;
import com.wzy.helptravel.utils.XmlParserHandler;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.Bind;

public class AreaActivity extends BaseToolBarActivity implements AdapterView.OnItemClickListener {


    @Override
    public String setTitle() {
        return "选择地区";
    }

    List<ProvinceModel> provinceList = null;
    private ListView provincelv;

    private ArrayAdapter<String> adapter;


    @Bind(R.id.area_province_tv)
    TextView areaTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        initBarView();
        initAdapter();
        provincelv = (ListView) findViewById(R.id.province_lv);
        provincelv.setAdapter(adapter);
        provincelv.setOnItemClickListener(this);

        areaTv.setText(getBundle().getCharSequence("area").toString());

    }

    private void initAdapter() {
        initXml();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        for (int i = 0; i < provinceList.size(); i++) {
            adapter.add(provinceList.get(i).getName());
        }
    }

    private void initXml() {
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ProvinceModel provinceModel = provinceList.get(position);
        final List<CityModel> cityList = provinceModel.getCityList();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayoutMain = new LinearLayout(this);//自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ListView cityListView = new ListView(this);
        cityListView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(builder.getContext(), android.R.layout.simple_expandable_list_item_1);
        for (int i = 0; i < cityList.size(); i++) {
            adapter.add(cityList.get(i).getName());
        }
        cityListView.setAdapter(adapter);
        linearLayoutMain.addView(cityListView);
        builder.setView(linearLayoutMain);

        final Dialog dialog = builder.show();

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                areaTv.setText(provinceModel.getName() + " " + cityList.get(position).getName());
                dialog.dismiss();
            }
        });


    }


    @Override
    public ToolBarListener setToolBarListener() {
        return new ToolBarListener() {
            @Override
            public void clickLeft() {
                back();
            }

            @Override
            public void clickRight() {

            }
        };
    }

    private void back(){
        String area = areaTv.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra("area", area);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        back();
    }
}
