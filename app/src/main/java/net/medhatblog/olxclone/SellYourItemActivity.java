package net.medhatblog.olxclone;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;

import net.medhatblog.olxclone.imagelib.activities.GalleryActivity;
import net.medhatblog.olxclone.imagelib.utils.Constants;
import net.medhatblog.olxclone.imagelib.utils.Image;
import net.medhatblog.olxclone.imagelib.utils.Params;

public class SellYourItemActivity extends AppCompatActivity {


    private ImageView addPhoto;
    private  int selectedColor;
    private ImageView imageView;
    private LinearLayout layout;
    private ArrayList<Image> imagesList;
    private ArrayList<Long> selectedIdsBackup;
    private TextView photoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_your_item);

        layout = (LinearLayout)findViewById(R.id.layout);
        imageView = (ImageView) findViewById(R.id.image1);
        selectedColor = fetchAccentColor();
        addPhoto = (ImageView) findViewById(R.id.add_photo);
        photoText = (TextView) findViewById(R.id.photo_text);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initiateMultiPicker();
            }
        });
    }
    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();

        params.setPickerLimit(8);

        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);

        if (selectedIdsBackup!=null){
            if (!selectedIdsBackup.isEmpty()) {
                intent.putExtra("selectedIdBack", selectedIdsBackup);
            }
        }

        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }
    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }


        imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);

        selectedIdsBackup = (ArrayList<Long>) intent.getSerializableExtra("selectedId");




        emptyViews();


        if (!imagesList.isEmpty()){
            for (int i =0 ;i<imagesList.size();i++){
                View v = layout.getChildAt(i+1);
                if (v instanceof ImageView){
                    Picasso.with(getApplicationContext())
                            .load(imagesList.get(i).uri).resize(100, 100)
                            .onlyScaleDown()
                            .centerInside()
                            .into((ImageView) v);

                    v.setVisibility(View.VISIBLE);
                }
            }

            String text= getString(R.string.photo_added,imagesList.size());
            photoText.setText(text);

        }else{
            photoText.setText(R.string.photo);

        }






    }

    public void emptyViews(){

        for (int i= 0;i<8;i++){
            View v = layout.getChildAt(i+1);
            if (v instanceof ImageView){

                imageView.setImageDrawable(null);
                v.setVisibility(View.GONE);

            }
        }
    }


}
