package pust.ice.eilish.api.Workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import pust.ice.eilish.Database.DbDao;
import pust.ice.eilish.Database.ElishDatabase;
import pust.ice.eilish.Database.Tables.DataTable;
import pust.ice.eilish.Database.Tables.InfoTable;
import pust.ice.eilish.Database.Tables.NotificationTable;
import pust.ice.eilish.ServerViewModel;
import pust.ice.eilish.api.Ainterface.ApiCall;
import pust.ice.eilish.api.Ainterface.Objects.CheckObject;
import pust.ice.eilish.api.Ainterface.Objects.FetchObject;
import pust.ice.eilish.api.Ainterface.Objects.Info;
import pust.ice.eilish.api.Api;
import pust.ice.eilish.nofications.NotificationWorker;
import retrofit2.Call;
import retrofit2.Response;

public class BackgroundWorker extends Worker {

    private Context context;
    private DbDao dbDao;
    private WorkManager workManager;

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dbDao = ElishDatabase.getINSTANCE(context).databaseDao();
        this.context = context;
        workManager = WorkManager.getInstance();
        ServerViewModel server = ServerViewModel.getinstance(getApplicationContext());

    }

    @NonNull
    @Override
    public Result doWork() {
        System.out.println("Fetching data from Server");
        ApiCall apiCall = Api.createService(ApiCall.class);
        Call<CheckObject> request = apiCall.performCheck();
        Response<CheckObject> res = null;
        try {
            res = request.execute();
            if (res.isSuccessful()) {
                List<Info> avail_data = res.body().getAvail_data(), local_data = dbDao.getlocaldata();
                List<Info> avail_noti = res.body().getAvail_notifications(), local_noti = dbDao.getlocalnotifications();
                List<Info> avail_info = res.body().getAvail_info(), local_info = dbDao.getlocalinfo();

                List<List<Integer>> dataresult = filterServerandlocal(avail_data, local_data);
                List<List<Integer>> notiresult = filterServerandlocal(avail_noti, local_noti);
                List<List<Integer>> inforesult = filterServerandlocal(avail_info, local_info);


                for (Integer i : dataresult.get(1)) {
                    DataTable d=dbDao.getdata(i);
                    Document doc = Jsoup.parse(d.getHtml());
                    Elements elems = doc.getElementsByTag("img");
                    for (Element el : elems) {
                        String filename = el.attr("src");
                        if(context.deleteFile(filename.substring(filename.lastIndexOf('/') + 1))){

                            break;
                        }
                    }
                    dbDao.deleteData(i);
                }
                for (Integer i : notiresult.get(1)) {
                    workManager.cancelUniqueWork("notification_task_" + i);
                    dbDao.deleteNotification(i);
                }
                for (Integer i : inforesult.get(1)) {
                    dbDao.deleteinfo(i);
                }


                List<Integer> getdata = dataresult.get(0), getnotifications = notiresult.get(0), getinfo = inforesult.get(0);
                if (getdata.size() + getnotifications.size() + inforesult.size() != 0) {
                    String querydata = "", querynoti = "", queryinfo = "";
                    if (getdata.size() != 0) {
                        querydata = formatstring(getdata.toString());
                    }
                    if (getnotifications.size() != 0) {
                        querynoti = formatstring(getnotifications.toString());
                    }
                    if (getinfo.size() != 0) {
                        queryinfo = formatstring(getinfo.toString());
                    }

                    Call<FetchObject> updaterequest = apiCall.getupdate(querydata, querynoti, queryinfo);
                    Response<FetchObject> updateresponse = null;
                    try {
                        updateresponse = updaterequest.execute();
                        FetchObject fetchedobject = updateresponse.body();
                        if (updateresponse.isSuccessful()) {
                           if(fetchedobject.getData()!=null)
                           {
                               for (DataTable d : fetchedobject.getData()) {
                                   Document doc = Jsoup.parse(d.getHtml());
                                   String fallback_font = "@font-face {\n" +
                                           "        font-family: kalpurush;\n" +
                                           "        src: url(\"file:///android_res/font/notosansbngaliregular.ttf\")\n" +
                                           "           }";
                                   String tagstyle = doc.getElementsByTag("style").html();
                                   if (!tagstyle.equals("")) {
                                       doc.getElementsByTag("style").append(fallback_font);
                                   } else {
                                       doc.head().append("<style type=\"text/css\">\n" + fallback_font + "\n</style>");
                                   }
                                   Elements table = doc.getElementsByTag("table");
                                   if (table.hasAttr("style")) {
                                       String style = table.attr("style");
                                       style = manipulateStyle(style);
                                       table.attr("style", style);
                                   } else {
                                       table.attr("style", "width:100%;height:auto");
                                   }


                                   Elements elems = doc.getElementsByTag("img");
                                   if (elems.hasAttr("style")) {
                                       String style = elems.attr("style");
                                       style = manipulateStyle(style);
                                       elems.attr("style", style);
                                   } else {
                                       elems.attr("style", "max-width:100%;height: auto;");
                                   }
                                   for (Element el : elems) {
                                       String filename = el.attr("src");
                                       Call<ResponseBody> call = apiCall.getimage(Api.URL+filename);
                                       Response<ResponseBody> response = call.execute();
                                       FileOutputStream outputStream = context.openFileOutput(filename.substring(filename.lastIndexOf('/') + 1), Context.MODE_PRIVATE);
                                       try {
                                           ResponseBody body = response.body();
                                           byte[] fileReader = new byte[4096];

                                           InputStream inputStream = body.byteStream();

                                           while (true) {
                                               int read = inputStream.read(fileReader);

                                               if (read == -1) {
                                                   break;
                                               }

                                               outputStream.write(fileReader, 0, read);
                                           }
                                           outputStream.flush();
                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       } finally {
                                           outputStream.close();
                                       }

                                       String picUri = "file://" + context.getFilesDir().getAbsolutePath() + "/" + filename.substring(filename.lastIndexOf('/') + 1);
                                       el.attr("src", picUri);
                                   }
                                   d.setHtml(doc.html());
                                   dbDao.insert(d);
                               }
                           }
                            if(fetchedobject.getNotifications()!=null)
                            {
                                for (NotificationTable n : fetchedobject.getNotifications()) {
                                    Long time = calculatetime(n.getDate(), n.getStartTime());
                                    if (time > 0) {
                                        System.out.println("Scheduling notification: " + n.getId());

                                        Data data = new Data.Builder()
                                                .putString("header", n.getHeader())
                                                .putInt("id", n.getId())
                                                .putString("body", n.getBody()).build();
                                        OneTimeWorkRequest notificationtask = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                                .setInitialDelay(time, TimeUnit.MILLISECONDS)
                                                .setInputData(data)
                                                .build();
                                        workManager.enqueueUniqueWork("notification_task_" + n.getId(), ExistingWorkPolicy.REPLACE, notificationtask);
                                    }
                                    dbDao.insert(n);
                                }
                            }
                           if(fetchedobject.getInfo()!=null)
                           {
                               for (InfoTable infoTable : fetchedobject.getInfo()) {
                                   dbDao.insert(infoTable);
                               }
                           }
                        } else {
                            //TODO Error in UpdateRequest
                            return Result.retry();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Result.retry();
                    }
                } else {
                    System.out.println("DB is uptodate");
                }
                return Result.success();
            } else {
                return Result.retry();
                //TODO Error in response
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (res != null) {
                e.printStackTrace();
            } else {
                System.out.println("Server is downn");
            }
            return Result.retry();
        }
    }

    private List<List<Integer>> filterServerandlocal(List<Info> avail_data, List<Info> local_data) {
        Map<Integer, String> server_map = new HashMap<>();
        List<Integer> update = new ArrayList<>(), delete = new ArrayList<>();
        for (Info i : avail_data) {
            server_map.put(i.getId(), i.getUpdated_at());
        }
        for (Info i : local_data) {
            if (server_map.containsKey(i.getId())) {
                if (!server_map.get(i.getId()).equals(i.getUpdated_at())) {
                    update.add(i.getId());
                }
                server_map.remove(i.getId());
            } else {
                delete.add(i.getId());
            }
        }
        update.addAll(server_map.keySet());

        List<List<Integer>> returnlist = new ArrayList<>();
        returnlist.add(update);
        returnlist.add(delete);
        return returnlist;
    }

    private String formatstring(String s) {
        s = s.substring(1, s.length() - 1);
        return s.replaceAll("\\s+", "");
    }

    private String manipulateStyle(String style) {
        String[] token = style.split(";");
        style = null;
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : token) {
            if (s.contains("width")) {
                stringBuffer.append("width: 100%;");
            } else if (s.contains("height")) {
                stringBuffer.append("height: auto;");
            } else {
                stringBuffer.append(s + ';');
            }
        }
        return stringBuffer.toString();
    }

    private Long calculatetime(String s, String t) {

        int f_day, f_month, f_year, f_hour, f_min;
        f_year = Integer.valueOf(s.substring(0, 4));
        f_month = Integer.valueOf(s.substring(5, 7));
        f_day = Integer.valueOf(s.substring(8, 10));
        f_hour = Integer.valueOf(t.substring(0, 2));
        f_min = Integer.valueOf(t.substring(3, 5));
        DateTime present = new DateTime();
        DateTime future = new DateTime(f_year, f_month, f_day, f_hour, f_min); //TODO Remove 1

        future = future.plusDays(1);
//        DateTime future=new DateTime();
//        future=future.plusMinutes(1);
//        System.out.println(present.toDateTime());
//        System.out.println(future.toDateTime());
        if (future.isAfterNow()) {
            Interval interval = new Interval(present, future);
            return interval.toDurationMillis();
        } else {
            System.out.println("Sorry future is past");
        }

        return 0L;
    }
}
