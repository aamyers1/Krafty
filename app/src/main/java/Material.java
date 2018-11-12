import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Material {
    private String name, image, purchased;
    private  int quantity;
    private  double price;
    Material(){
    }

    Material(String name, String image, int quantity,
             double price){
        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        purchased = (sdf.format(date));;
    }

    public JSONObject createJson(){
        JSONObject json = new JSONObject();
        try{
            json.put("name", name);
            json.put("image", image);
            json.put("quantity", quantity);
            json.put("price", price);
            //json.put("purchased",purchased);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public Material parseJson(JSONObject json){
        try{
            this.name = json.getJSONObject("").getString("name");
            this.image = json.getJSONObject("").getString("image");
            this.quantity = json.getJSONObject("").getInt("quantity");
            this.price = json.getJSONObject("").getDouble("price");
            this.purchased = json.getJSONObject("").getString("purchased");
        }
        catch (Exception e){

        }
        return  this;
    }
}
