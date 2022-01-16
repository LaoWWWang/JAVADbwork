package west2dbwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ll {

    public static JSONObject sendGet(String url) {//用Get请求url
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        JSONObject json_res = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));                //headers信息
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
            String line;
            in = new BufferedReader(new
                    InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            json_res = JSONObject.parseObject(String.valueOf(result));
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return json_res;
    }

    public static void main(String[] args) {
        List<City_information> listOfCity_inf = new ArrayList<>();
        String key = "";                                                            //记得写key
        String url = "https://geoapi.qweather.com/v2/city/lookup?key=" + key + "&location=";
        String url1 = "https://devapi.qweather.com/v7/weather/3d?key=" + key + "&location=";
        int city_id,page;
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入查询城市");
        String city_name = scanner.nextLine();
        try {
            city_name = URLEncoder.encode(city_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONObject res_city_inf = sendGet(url + city_name).getJSONArray("location").getJSONObject(0);
        listOfCity_inf.add(new City_information(res_city_inf.getString("name"), res_city_inf.getInteger("id"),
                res_city_inf.getFloat("lat"), res_city_inf.getFloat("lon")));//这里其实我感觉可以直接存入数据库了，
        String json_out = JSON.toJSONString(listOfCity_inf);                          //不需要二次处理，但我总感觉哪里不对，
        JSONArray city_inf = JSONArray.parseArray(json_out);                          //所以还是留一个单独分离出来的JSON。
        JSONObject city_inf1 = city_inf.getJSONObject(0);
        Db.addCity(city_inf1.getString("name"), city_inf1.getInteger("id"),
                city_inf1.getFloat("lat"), city_inf1.getFloat("lon"));
        city_id = city_inf1.getInteger("id");
        JSONArray res_city_wea = sendGet(url1 + city_id).getJSONArray("daily");
        for (int i = 0; i < res_city_wea.size(); i++) {         //上个城市信息我把需要的信息单独取出来重新JSON,但我感觉好像没什么必要，
            JSONObject city_wea = res_city_wea.getJSONObject(i);//因为直接取出信息存入也是一样的效果，所以这个weather信息我就没单独处理。
            Db.addCityTemp(city_id,city_wea.getString("fxDate"),city_wea.getInteger("tempMax"),
                    city_wea.getInteger("tempMin"),city_wea.getString("textDay"));
        }
        System.out.println("查询第几页");
        page = scanner.nextInt();
        Db.search(page);
        System.out.println("查询哪座城市信息(数据库中存在的)");
        scanner.nextLine();
        city_name = scanner.nextLine();
        Db.search(city_name);
    }
}

class City_information {

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "id")
    private int id;

    @JSONField(name = "lat")
    private float lat;

    @JSONField(name = "lon")
    private float lon;

    public City_information(String name, int id, float lat, float lon) {
        super();
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setLon(float lon) {
        this.lon = lon;
    }


    public void setLat(float lat) {
        this.lat = lat;
    }


    public void setId(int id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getLon() {
        return lon;
    }
}

