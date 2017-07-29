package net.medhatblog.olxclone;

/**
 * Created by PC on 7/29/2017.
 */

public class AdUploadInfo {

    public String adTitle;
    public String adPrice;
    public String adDescription;
    public String adName;
    public String adEmail;
    public String adPhone;





    public AdUploadInfo() {

    }

    public AdUploadInfo(String title, String price,String description,
                           String name,String email,String phone) {

        this.adTitle= title;
        this.adPrice = price;
        this.adDescription = description;
        this.adName = name;
        this.adEmail = email;
        this.adPhone = phone;

    }


}
