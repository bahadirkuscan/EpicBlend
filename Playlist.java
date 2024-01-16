public class Playlist {
    public int id;
    public int[] limits = new int[3];
    public MinHeap heartache_blend_songs;
    public MinHeap roadtrip_blend_songs;
    public MinHeap blissful_blend_songs;

    // Max heaps store the songs in the playlist those are not in the epicblend
    public MaxHeap heartache_songs;
    public MaxHeap roadtrip_songs;
    public MaxHeap blissful_songs;

    Playlist(){
        id = 0;
    }
    Playlist(int id, int limit){
        this.id = id;
        limits[0] = limit;
        limits[1] = limit;
        limits[2] = limit;

        heartache_blend_songs = new MinHeap(limit + 1, 0);
        roadtrip_blend_songs = new MinHeap(limit + 1, 1);
        blissful_blend_songs = new MinHeap(limit + 1, 2);

        heartache_songs = new MaxHeap(0);
        roadtrip_songs = new MaxHeap(1);
        blissful_songs = new MaxHeap(2);
    }

    // returns 1 if "this" playlist has higher max scored song
    public int comparePlaylist(Playlist playlist, int score_type){
        if (score_type == 0){
            return this.heartache_songs.getMax().compareScore(playlist.heartache_songs.getMax(), 0);
        }
        else if (score_type == 1){
            return this.roadtrip_songs.getMax().compareScore(playlist.roadtrip_songs.getMax(), 1);
        }
        return this.blissful_songs.getMax().compareScore(playlist.blissful_songs.getMax(), 2);
    }
}
