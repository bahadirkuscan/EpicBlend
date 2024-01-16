public class Song {
    public String name;
    public int id;
    public Playlist playlist;
    public int play_count;
    public int[] scores = new int[3];
    public boolean is_in_blend = false;


    Song(){  // for non-existent songs
        scores[0] = -1;
        scores[1] = -1;
        scores[2] = -1;
        id = 0;
        name = "";
        playlist = new Playlist();
    }

    Song(int id, String name, int play_count, int heartache_score, int roadtrip_score, int blissful_score){
        this.id = id;
        this.name = name;
        this.play_count = play_count;
        scores[0] = heartache_score;
        scores[1] = roadtrip_score;
        scores[2] = blissful_score;
    }

    // comparison methods return 1 if "this" is prioritized
    public int compareScore(Song song, int score_type){
        if (scores[score_type] > song.scores[score_type]){
            return 1;
        }
        else if (scores[score_type] < song.scores[score_type]){
            return -1;
        }
        else{
            return name.compareTo(song.name) * -1;
        }
    }

    public int comparePlaycount(Song song){
        if (play_count > song.play_count){
            return 1;
        }
        else if (play_count < song.play_count){
            return -1;
        }
        else{
            return name.compareTo(song.name) * -1;
        }
    }
}
