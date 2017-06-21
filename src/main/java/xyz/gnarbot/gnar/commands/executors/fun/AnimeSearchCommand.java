//package xyz.gnarbot.gnar.commands.executors.fun;
//
//import com.rethinkdb.gen.ast.Json;
//import net.dv8tion.jda.core.entities.Emote;
//import net.dv8tion.jda.core.entities.Message;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import xyz.gnarbot.gnar.Bot;
//import xyz.gnarbot.gnar.commands.Category;
//import xyz.gnarbot.gnar.commands.Command;
//import xyz.gnarbot.gnar.commands.CommandExecutor;
//import xyz.gnarbot.gnar.utils.Context;
//
//import java.util.Iterator;
//
///**
// * Created by Gatt on 20/06/2017.
// */
//@Command(aliases = {"anime", "animesearch", "malanime"}, usage = "{anime name}", description = "Search for an Anime via MyAnimeList", category = Category.FUN)
//public class AnimeSearchCommand extends CommandExecutor {
//    @Override
//    public void execute(Context context, String[] args) {
//        if (args.length < 1){
//            context.send().error("No arguments provided. Grrr! `_anime [search term]`").complete();
//        }else{
//            if (!Bot.getMALAPI().isLoggedIn()){
//                context.send().error("Please wait one second! I'm still logging in!").complete();
//                return;
//            }
//            String animeSearch = StringUtils.join(args, "%20");
//            JSONObject obj = Bot.getMALAPI().makeRequest(Bot.getMALAPI().SEARCH_ANIME, "q=" + animeSearch);
//
//            if (!obj.has("anime")){
//                context.send().error("Nothing found with that search term!").complete();
//                return;
//            }
//            JSONArray animeObject = obj.getJSONObject("anime").getJSONArray("entry");
//            String amountFoundMsg = animeObject.length() < 3 ? "Here are the top " + animeObject.length() + " results found." :
//                    "Here are the top 3 results found (Out of " + animeObject.length() + ").";
//            String returnMessage = "";
//            int idCheck = 0;
//            Iterator iterator = animeObject.iterator();
//            String img = null;
//            while (iterator.hasNext() && idCheck < 3){
//                idCheck++;
//                Object o = iterator.next();
//                if (o instanceof JSONObject){
//                    JSONObject entry = (JSONObject)o;
//                    if (img == null){
//                        img = entry.getString("image");
//                    }
//                    String newEntry = (idCheck) + "\u20E3  [%animeenglish%]() (%animetitle%)\n" +
//                            "  [__Episodes__](): **%episodes%**\n" +
//                            "  [__Score__](): **%score%**\n" +
//                            "  [__Status__](): **%status%**\n" +
//                            "  [__Start Date__](): **%startdate%**\n" +
//                            "  [__End Date__](): **%enddate%**\n\n" +
//                            "  [__**Synopsis**__]()\n*%synopsis%*";
//                    newEntry = newEntry.replaceAll("%animetitle%", entry.getString("title").length() < 20 ? entry.getString("title") :
//                            entry.getString("title").substring(0, 17) + "...");
//                    newEntry = newEntry.replaceAll("%animeenglish%", entry.getString("english").length() < 20 ? entry.getString("english") :
//                            entry.getString("english").substring(0, 17) + "...");
//                    newEntry = newEntry.replaceAll("%episodes%", entry.getInt("episodes") + "");
//                    newEntry = newEntry.replaceAll("%status%", entry.getString("status"));
//                    newEntry = newEntry.replaceAll("%enddate%", entry.getString("end_date"));
//                    newEntry = newEntry.replaceAll("%startdate%", entry.getString("start_date"));
//                    newEntry = newEntry.replaceAll("%score%", entry.getDouble("score") + "/10 *(Average)*");
//                    String synopsis = Jsoup.parse(entry.getString("synopsis")).text().replaceAll("\\[i]", "").replaceAll("\\[/i]", "");
//                    newEntry = newEntry.replaceAll("%synopsis%", synopsis.length() >= 150 ?
//                            synopsis.substring(0, 147) + "..." :
//                            synopsis);
//                    returnMessage += newEntry +"\n\n";
//                }
//            }
//            returnMessage += "\n\n**Select a reaction below to get more information on the selected choice!**";
//            Message msg = context.send().embed(amountFoundMsg).setDescription(returnMessage).setThumbnail(img).action().complete();
//            for (int reactID = 1; reactID <= idCheck; reactID++){
//                msg.addReaction(reactID + "\u20E3").queue();
//                msg.addReaction(reactID + "\u20E3").queue();
//                msg.addReaction(reactID + "\u20E3").queue();
//            }
//        }
//    }
//}
