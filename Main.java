import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) throws FileNotFoundException {

        PrintWriter outwriter = new PrintWriter(args[2]);
        Scanner song_file = new Scanner(new File(args[0]));
        Scanner case_file = new Scanner(new File(args[1]));


        Song[] songs;
        Playlist[] playlists;
        HashSet<Song> blend_songs = new HashSet<>();

        MinHeap min_heartache_blend_songs_of_playlists;
        MinHeap min_roadtrip_blend_songs_of_playlists;
        MinHeap min_blissful_blend_songs_of_playlists;

        // Max heaps store playlists which can contribute more songs to the blend (limit is not reached and there exists at least 1 song)
        MaxHeapPlaylist heartache_available_playlists;
        MaxHeapPlaylist roadtrip_available_playlists;
        MaxHeapPlaylist blissful_available_playlists;

        // READING SONGS
        int total_song_count = Integer.parseInt(song_file.nextLine());
        songs = new Song[total_song_count + 1];
        for (int i = 1; i <= total_song_count; i++){
            String[] line = song_file.nextLine().split(" ");
            songs[i] = new Song(Integer.parseInt(line[0]), line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]), Integer.parseInt(line[4]), Integer.parseInt(line[5]));
        }

        song_file.close();

        String[] line = case_file.nextLine().split(" ");
        int playlist_limit = Integer.parseInt(line[0]);
        int blend_heartache_limit = Integer.parseInt(line[1]);
        int blend_roadtrip_limit = Integer.parseInt(line[2]);
        int blend_blissful_limit = Integer.parseInt(line[3]);

        // READING PLAYLISTS
        int playlist_count = Integer.parseInt(case_file.nextLine());
        heartache_available_playlists = new MaxHeapPlaylist(playlist_count, 0);
        roadtrip_available_playlists = new MaxHeapPlaylist(playlist_count, 1);
        blissful_available_playlists = new MaxHeapPlaylist(playlist_count, 2);
        playlists = new Playlist[playlist_count + 1];
        min_heartache_blend_songs_of_playlists = new MinHeap(playlist_count + 1, 0);
        min_roadtrip_blend_songs_of_playlists = new MinHeap(playlist_count + 1, 1);
        min_blissful_blend_songs_of_playlists = new MinHeap(playlist_count + 1, 2);
        for (int i = 1; i <= playlist_count; i++){
            int playlist_id = Integer.parseInt(case_file.next());
            int playlist_song_count = Integer.parseInt(case_file.next());
            Playlist playlist = new Playlist(playlist_id, playlist_limit);
            playlists[i] = playlist;
            for (int k = 0; k < playlist_song_count; k++){
                Song song = songs[Integer.parseInt(case_file.next())];
                song.playlist = playlist;
                playlist.heartache_songs.append(song);
                playlist.roadtrip_songs.append(song);
                playlist.blissful_songs.append(song);
            }
            playlist.heartache_songs.buildHeap();
            playlist.roadtrip_songs.buildHeap();
            playlist.blissful_songs.buildHeap();

            Song max1 = playlist.heartache_songs.getMax();
            if (max1.id != 0){
                heartache_available_playlists.append(playlist);
            }
            Song max2 = playlist.roadtrip_songs.getMax();
            if (max2.id != 0){
                roadtrip_available_playlists.append(playlist);
            }
            Song max3 = playlist.blissful_songs.getMax();
            if (max3.id != 0){
                blissful_available_playlists.append(playlist);
            }
        }
        heartache_available_playlists.buildHeap();
        roadtrip_available_playlists.buildHeap();
        blissful_available_playlists.buildHeap();

        // EPICBLEND INITIALIZATION
        for (int limit = blend_heartache_limit; limit > 0; limit--){
            Playlist found_playlist = heartache_available_playlists.getMax();
            if (found_playlist.id == 0){
                break;
            }

            Song found_song = found_playlist.heartache_songs.deleteMax();
            found_song.is_in_blend = true;
            blend_songs.add(found_song);

            found_playlist.limits[0]--;
            blend_heartache_limit--;

            // updates in found song's playlist's heaps
            Song former_playlist_min = found_playlist.heartache_blend_songs.getMin();
            found_playlist.heartache_blend_songs.add(found_song);

            // updates in Main's heaps
            if (found_playlist.limits[0] == 0 || found_playlist.heartache_songs.getMax().id == 0){
                heartache_available_playlists.deleteMax();
            }
            else {
                heartache_available_playlists.percolateDown(1);
            }
            if (former_playlist_min.id != 0){
                min_heartache_blend_songs_of_playlists.replace(former_playlist_min, found_song);
            }
            else {
                min_heartache_blend_songs_of_playlists.add(found_song);
            }
        }

        for (int limit = blend_roadtrip_limit; limit > 0; limit--){
            Playlist found_playlist = roadtrip_available_playlists.getMax();
            if (found_playlist.id == 0){
                break;
            }

            Song found_song = found_playlist.roadtrip_songs.deleteMax();
            found_song.is_in_blend = true;
            blend_songs.add(found_song);

            found_playlist.limits[1]--;
            blend_roadtrip_limit--;

            // updates in found song's playlist's heaps
            Song former_playlist_min = found_playlist.roadtrip_blend_songs.getMin();
            found_playlist.roadtrip_blend_songs.add(found_song);

            // updates in Main's heaps
            if (found_playlist.limits[1] == 0 || found_playlist.roadtrip_songs.getMax().id == 0){
                roadtrip_available_playlists.deleteMax();
            }
            else {
                roadtrip_available_playlists.percolateDown(1);
            }
            if (former_playlist_min.id != 0){
                min_roadtrip_blend_songs_of_playlists.replace(former_playlist_min, found_song);
            }
            else {
                min_roadtrip_blend_songs_of_playlists.add(found_song);
            }
        }

        for (int limit = blend_blissful_limit; limit > 0; limit--){
            Playlist found_playlist = blissful_available_playlists.getMax();
            if (found_playlist.id == 0){
                break;
            }

            Song found_song = found_playlist.blissful_songs.deleteMax();
            found_song.is_in_blend = true;
            blend_songs.add(found_song);

            found_playlist.limits[2]--;
            blend_blissful_limit--;

            // updates in found song's playlist's heaps
            Song former_playlist_min = found_playlist.blissful_blend_songs.getMin();
            found_playlist.blissful_blend_songs.add(found_song);

            // updates in Main's heaps
            if (found_playlist.limits[2] == 0 || found_playlist.blissful_songs.getMax().id == 0){
                blissful_available_playlists.deleteMax();
            }
            else {
                blissful_available_playlists.percolateDown(1);
            }
            if (former_playlist_min.id != 0){
                min_blissful_blend_songs_of_playlists.replace(former_playlist_min, found_song);
            }
            else {
                min_blissful_blend_songs_of_playlists.add(found_song);
            }
        }


        // READING EVENTS
        int event_count = Integer.parseInt(case_file.next());
        case_file.nextLine();
        for (int i = 0; i < event_count; i++){
            String[] ln = case_file.nextLine().split(" ");

            switch (ln[0]){
                case "ADD":
                    Song song = songs[Integer.parseInt(ln[1])];
                    song.playlist = playlists[Integer.parseInt(ln[2])];
                    Playlist playlist = song.playlist;
                    int replaced_heartache_id = 0;
                    int replaced_roadtrip_id = 0;
                    int replaced_blissful_id = 0;
                    int h_added = song.id;
                    int r_added = song.id;
                    int b_added = song.id;

                    // playlist and blend has space, directly add to blend
                    if (playlist.limits[0] > 0 && blend_heartache_limit > 0){
                        // update Main's minheap
                        Song former_min = playlist.heartache_blend_songs.getMin();
                        if (former_min.id == 0){
                            min_heartache_blend_songs_of_playlists.add(song);
                        }
                        else if (song.compareScore(former_min, 0) < 0){
                            min_heartache_blend_songs_of_playlists.replace(former_min, song);
                        }
                        // update playlist's heap
                        playlist.heartache_blend_songs.add(song);
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.limits[0]--;
                        // update Main's maxheap
                        if (playlist.limits[0] == 0 && heartache_available_playlists.contains(playlist)){
                            heartache_available_playlists.remove(playlist);
                        }
                        blend_heartache_limit--;
                    }

                    // playlist has space, blend doesn't, replace the absolute min scored song in blend
                    else if (playlist.limits[0] > 0 && song.compareScore(min_heartache_blend_songs_of_playlists.getMin(), 0) > 0){
                        Song replaced_heartache = min_heartache_blend_songs_of_playlists.getMin();
                        Playlist replaced_playlist = replaced_heartache.playlist;
                        if (!replaced_playlist.roadtrip_blend_songs.contains(replaced_heartache) && !replaced_playlist.blissful_blend_songs.contains(replaced_heartache)){
                            replaced_heartache.is_in_blend = false;
                            blend_songs.remove(replaced_heartache);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);

                        // case 1: replaced and added songs are in distinct playlists
                        if (playlist != replaced_playlist){
                            // update replaced song's playlist's heaps
                            replaced_playlist.limits[0]++;
                            replaced_playlist.heartache_blend_songs.deleteMin();
                            Song new_min = replaced_playlist.heartache_blend_songs.getMin();

                            Song former_max = replaced_playlist.heartache_songs.getMax();   // we know for sure that replaced song will be the new max of the playlist
                            replaced_playlist.heartache_songs.add(replaced_heartache);
                            // update Main's heaps
                            if (new_min.id != 0){
                                min_heartache_blend_songs_of_playlists.replace(replaced_heartache, new_min);
                            }
                            else {
                                min_heartache_blend_songs_of_playlists.deleteMin();
                            }
                            if (former_max.id == 0 || replaced_playlist.limits[0] == 1){    // former max will for sure be replaced if it exists, no need for score comparison
                                heartache_available_playlists.add(replaced_playlist);
                            }
                            else {
                                heartache_available_playlists.updatePlace(replaced_playlist);
                            }


                            // update added song's playlist's heap
                            Song former_min = playlist.heartache_blend_songs.getMin();
                            playlist.heartache_blend_songs.add(song);
                            playlist.limits[0]--;

                            // update Main's heaps
                            if (playlist.limits[0] == 0 && heartache_available_playlists.contains(playlist)){
                                heartache_available_playlists.remove(playlist);
                            }
                            if (former_min.id == 0){
                                min_heartache_blend_songs_of_playlists.add(song);
                            }
                            else if (song.compareScore(former_min, 0) < 0){
                                min_heartache_blend_songs_of_playlists.replace(former_min, song);
                            }

                        }
                        // case 2: absolute min is in added song's playlist
                        else {
                            playlist.heartache_blend_songs.replace(replaced_heartache,song);
                            Song new_min = playlist.heartache_blend_songs.getMin();
                            min_heartache_blend_songs_of_playlists.replace(replaced_heartache, new_min);

                            Song former_max = playlist.heartache_songs.getMax();
                            playlist.heartache_songs.add(replaced_heartache);
                            if (former_max.id == 0){
                                heartache_available_playlists.add(playlist);
                            }
                            else {
                               heartache_available_playlists.updatePlace(playlist);
                            }
                        }
                        replaced_heartache_id = replaced_heartache.id;
                    }
                    // playlist has no space, replace the playlist's min scored song
                    else if (playlist.limits[0] == 0 && song.compareScore(playlist.heartache_blend_songs.getMin(), 0) > 0) {
                        Song replaced_heartache = playlist.heartache_blend_songs.getMin();
                        if (!playlist.roadtrip_blend_songs.contains(replaced_heartache) && !playlist.blissful_blend_songs.contains(replaced_heartache)){
                            replaced_heartache.is_in_blend = false;
                            blend_songs.remove(replaced_heartache);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.heartache_blend_songs.replace(replaced_heartache, song);
                        Song new_min = playlist.heartache_blend_songs.getMin();
                        min_heartache_blend_songs_of_playlists.replace(replaced_heartache, new_min);

                        playlist.heartache_songs.add(replaced_heartache);

                        replaced_heartache_id = replaced_heartache.id;
                    }
                    else{   // song is not added to blend
                        Song former_max = playlist.heartache_songs.getMax();
                        playlist.heartache_songs.add(song);
                        if (playlist.limits[0] > 0){
                            if (former_max.id == 0){
                                heartache_available_playlists.add(playlist);
                            }
                            else if (song.compareScore(former_max, 0) > 0){
                                heartache_available_playlists.updatePlace(playlist);
                            }
                        }

                        h_added = 0;
                    }


                    // same procedure for other 2 score types

                    // playlist and blend has space, directly add to blend
                    if (playlist.limits[1] > 0 && blend_roadtrip_limit > 0){
                        // update Main's heap
                        Song former_min = playlist.roadtrip_blend_songs.getMin();
                        if (former_min.id == 0){
                            min_roadtrip_blend_songs_of_playlists.add(song);
                        }
                        else if (song.compareScore(former_min, 1) < 0){
                            min_roadtrip_blend_songs_of_playlists.replace(former_min, song);
                        }
                        // update playlist's heap
                        playlist.roadtrip_blend_songs.add(song);
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.limits[1]--;
                        if (playlist.limits[1] == 0 && roadtrip_available_playlists.contains(playlist)){
                            roadtrip_available_playlists.remove(playlist);
                        }
                        blend_roadtrip_limit--;
                    }

                    // playlist has space, blend doesn't, replace the absolute min scored song in blend
                    else if (playlist.limits[1] > 0 && song.compareScore(min_roadtrip_blend_songs_of_playlists.getMin(), 1) > 0){
                        Song replaced_roadtrip = min_roadtrip_blend_songs_of_playlists.getMin();
                        Playlist replaced_playlist = replaced_roadtrip.playlist;
                        if (!replaced_playlist.heartache_blend_songs.contains(replaced_roadtrip) && !replaced_playlist.blissful_blend_songs.contains(replaced_roadtrip)){
                            replaced_roadtrip.is_in_blend = false;
                            blend_songs.remove(replaced_roadtrip);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);

                        // replaced and added songs are in distinct playlists
                        if (playlist != replaced_playlist){
                            // update replaced song's playlist's heaps
                            replaced_playlist.limits[1]++;
                            replaced_playlist.roadtrip_blend_songs.deleteMin();
                            Song new_min = replaced_playlist.roadtrip_blend_songs.getMin();

                            Song former_max = replaced_playlist.roadtrip_songs.getMax();   // we know for sure that replaced song will be the new max of the playlist
                            replaced_playlist.roadtrip_songs.add(replaced_roadtrip);
                            // update Main's heaps
                            if (new_min.id != 0){
                                min_roadtrip_blend_songs_of_playlists.replace(replaced_roadtrip, new_min);
                            }
                            else {
                                min_roadtrip_blend_songs_of_playlists.deleteMin();
                            }
                            if (former_max.id == 0 || replaced_playlist.limits[1] == 1){    // former max will for sure be replaced if it exists, no need for score comparison
                                roadtrip_available_playlists.add(replaced_playlist);
                            }
                            else {
                                roadtrip_available_playlists.updatePlace(replaced_playlist);
                            }


                            // update added song's playlist's heap
                            Song former_min = playlist.roadtrip_blend_songs.getMin();
                            playlist.roadtrip_blend_songs.add(song);
                            playlist.limits[1]--;

                            // update Main's heaps
                            if (playlist.limits[1] == 0 && roadtrip_available_playlists.contains(playlist)){
                                roadtrip_available_playlists.remove(playlist);
                            }
                            if (former_min.id == 0){
                                min_roadtrip_blend_songs_of_playlists.add(song);
                            }
                            else if (song.compareScore(former_min, 1) < 0){
                                min_roadtrip_blend_songs_of_playlists.replace(former_min, song);
                            }

                        }
                        // absolute min is in added song's playlist
                        else {
                            playlist.roadtrip_blend_songs.replace(replaced_roadtrip,song);
                            Song new_min = playlist.roadtrip_blend_songs.getMin();
                            min_roadtrip_blend_songs_of_playlists.replace(replaced_roadtrip, new_min);

                            Song former_max = playlist.roadtrip_songs.getMax();
                            playlist.roadtrip_songs.add(replaced_roadtrip);
                            if (former_max.id == 0){
                                roadtrip_available_playlists.add(playlist);
                            }
                            else {
                                roadtrip_available_playlists.updatePlace(playlist);
                            }
                        }
                        replaced_roadtrip_id = replaced_roadtrip.id;
                    }
                    // playlist has no space, replace the playlist's min scored song
                    else if (playlist.limits[1] == 0 && song.compareScore(playlist.roadtrip_blend_songs.getMin(), 1) > 0) {
                        Song replaced_roadtrip = playlist.roadtrip_blend_songs.getMin();
                        if (!playlist.heartache_blend_songs.contains(replaced_roadtrip) && !playlist.blissful_blend_songs.contains(replaced_roadtrip)){
                            replaced_roadtrip.is_in_blend = false;
                            blend_songs.remove(replaced_roadtrip);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.roadtrip_blend_songs.replace(replaced_roadtrip, song);
                        Song new_min = playlist.roadtrip_blend_songs.getMin();
                        min_roadtrip_blend_songs_of_playlists.replace(replaced_roadtrip, new_min);

                        playlist.roadtrip_songs.add(replaced_roadtrip);

                        replaced_roadtrip_id = replaced_roadtrip.id;
                    }
                    else{   // song is not added to blend
                        Song former_max = playlist.roadtrip_songs.getMax();
                        playlist.roadtrip_songs.add(song);
                        if (playlist.limits[1] > 0){
                            if (former_max.id == 0){
                                roadtrip_available_playlists.add(playlist);
                            }
                            else if (song.compareScore(former_max, 1) > 0){
                                roadtrip_available_playlists.updatePlace(playlist);
                            }
                        }

                        r_added = 0;
                    }


                    // playlist and blend has space, directly add to blend
                    if (playlist.limits[2] > 0 && blend_blissful_limit > 0){
                        // update Main's heap
                        Song former_min = playlist.blissful_blend_songs.getMin();
                        if (former_min.id == 0){
                            min_blissful_blend_songs_of_playlists.add(song);
                        }
                        else if (song.compareScore(former_min, 2) < 0){
                            min_blissful_blend_songs_of_playlists.replace(former_min, song);
                        }
                        // update playlist's heap
                        playlist.blissful_blend_songs.add(song);
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.limits[2]--;
                        if (playlist.limits[2] == 0 && blissful_available_playlists.contains(playlist)){
                            blissful_available_playlists.remove(playlist);
                        }
                        blend_blissful_limit--;
                    }

                    // playlist has space, blend doesn't, replace the absolute min scored song in blend
                    else if (playlist.limits[2] > 0 && song.compareScore(min_blissful_blend_songs_of_playlists.getMin(), 2) > 0){
                        Song replaced_blissful = min_blissful_blend_songs_of_playlists.getMin();
                        Playlist replaced_playlist = replaced_blissful.playlist;
                        if (!replaced_playlist.roadtrip_blend_songs.contains(replaced_blissful) && !replaced_playlist.heartache_blend_songs.contains(replaced_blissful)){
                            replaced_blissful.is_in_blend = false;
                            blend_songs.remove(replaced_blissful);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);

                        // replaced and added songs are in distinct playlists
                        if (playlist != replaced_playlist){
                            // update replaced song's playlist's heaps
                            replaced_playlist.limits[2]++;
                            replaced_playlist.blissful_blend_songs.deleteMin();
                            Song new_min = replaced_playlist.blissful_blend_songs.getMin();

                            Song former_max = replaced_playlist.blissful_songs.getMax();   // we know for sure that replaced song will be the new max of the playlist
                            replaced_playlist.blissful_songs.add(replaced_blissful);
                            // update Main's heaps
                            if (new_min.id != 0){
                                min_blissful_blend_songs_of_playlists.replace(replaced_blissful, new_min);
                            }
                            else {
                                min_blissful_blend_songs_of_playlists.deleteMin();
                            }
                            if (former_max.id == 0 || replaced_playlist.limits[2] == 1){    // former max will for sure be replaced if it exists, no need for score comparison
                                blissful_available_playlists.add(replaced_playlist);
                            }
                            else {
                                blissful_available_playlists.updatePlace(replaced_playlist);
                            }


                            // update added song's playlist's heap
                            Song former_min = playlist.blissful_blend_songs.getMin();
                            playlist.blissful_blend_songs.add(song);
                            playlist.limits[2]--;

                            // update Main's heaps
                            if (playlist.limits[2] == 0 && blissful_available_playlists.contains(playlist)){
                                blissful_available_playlists.remove(playlist);
                            }
                            if (former_min.id == 0){
                                min_blissful_blend_songs_of_playlists.add(song);
                            }
                            else if (song.compareScore(former_min, 2) < 0){
                                min_blissful_blend_songs_of_playlists.replace(former_min, song);
                            }

                        }
                        // absolute min is in added song's playlist
                        else {
                            playlist.blissful_blend_songs.replace(replaced_blissful,song);
                            Song new_min = playlist.blissful_blend_songs.getMin();
                            min_blissful_blend_songs_of_playlists.replace(replaced_blissful, new_min);

                            Song former_max = playlist.blissful_songs.getMax();
                            playlist.blissful_songs.add(replaced_blissful);
                            if (former_max.id == 0){
                                blissful_available_playlists.add(playlist);
                            }
                            else {
                                blissful_available_playlists.updatePlace(playlist);
                            }
                        }
                        replaced_blissful_id = replaced_blissful.id;
                    }
                    // playlist has no space, replace the playlist's min scored song
                    else if (playlist.limits[2] == 0 && song.compareScore(playlist.blissful_blend_songs.getMin(), 2) > 0) {
                        Song replaced_blissful = playlist.blissful_blend_songs.getMin();
                        if (!playlist.roadtrip_blend_songs.contains(replaced_blissful) && !playlist.heartache_blend_songs.contains(replaced_blissful)){
                            replaced_blissful.is_in_blend = false;
                            blend_songs.remove(replaced_blissful);
                        }
                        song.is_in_blend = true;
                        blend_songs.add(song);
                        playlist.blissful_blend_songs.replace(replaced_blissful, song);
                        Song new_min = playlist.blissful_blend_songs.getMin();
                        min_blissful_blend_songs_of_playlists.replace(replaced_blissful, new_min);

                        playlist.blissful_songs.add(replaced_blissful);

                        replaced_blissful_id = replaced_blissful.id;
                    }
                    else{   // song is not added to blend
                        Song former_max = playlist.blissful_songs.getMax();
                        playlist.blissful_songs.add(song);
                        if (playlist.limits[2] > 0){
                            if (former_max.id == 0){
                                blissful_available_playlists.add(playlist);
                            }
                            else if (song.compareScore(former_max, 2) > 0){
                                blissful_available_playlists.updatePlace(playlist);
                            }
                        }

                        b_added = 0;
                    }
                    outwriter.write(String.format("%d %d %d\n%d %d %d\n", h_added, r_added, b_added, replaced_heartache_id, replaced_roadtrip_id, replaced_blissful_id));
                    break;






                case "REM":
                    Song removed_song = songs[Integer.parseInt(ln[1])];
                    Playlist removed_playlist = playlists[Integer.parseInt(ln[2])];
                    int removed_h = 0;
                    int removed_r = 0;
                    int removed_b = 0;
                    int replacer_h = 0;
                    int replacer_r = 0;
                    int replacer_b = 0;


                    if (removed_song.is_in_blend){
                        removed_song.is_in_blend = false;
                        blend_songs.remove(removed_song);



                        // removed song participated in heartache category
                        if (removed_playlist.heartache_blend_songs.remove(removed_song)){
                            removed_h = removed_song.id;
                            removed_playlist.limits[0]++;
                            // update main's heaps
                            if (removed_playlist.limits[0] == 1 && removed_playlist.heartache_songs.size > 0){
                                heartache_available_playlists.add(removed_playlist);
                            }
                            blend_heartache_limit++;

                            Song new_min = removed_playlist.heartache_blend_songs.getMin();
                            if (new_min.id == 0){
                                min_heartache_blend_songs_of_playlists.remove(removed_song);
                            }
                            else if (removed_song.compareScore(new_min, 0) < 0){
                                min_heartache_blend_songs_of_playlists.replace(removed_song, new_min);
                            }
                            // update other score types' max heaps
                            if (removed_playlist.roadtrip_songs.contains(removed_song)){
                                removed_playlist.roadtrip_songs.remove(removed_song);
                                Song new_max = removed_playlist.roadtrip_songs.getMax();
                                if (new_max.id != 0){
                                    if (roadtrip_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 1) > 0){
                                        roadtrip_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (roadtrip_available_playlists.contains(removed_playlist)){
                                    roadtrip_available_playlists.remove(removed_playlist);
                                }
                            }

                            if (removed_playlist.blissful_songs.contains(removed_song)){
                                removed_playlist.blissful_songs.remove(removed_song);
                                Song new_max = removed_playlist.blissful_songs.getMax();
                                if (new_max.id != 0){
                                    if (blissful_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 2) > 0){
                                        blissful_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (blissful_available_playlists.contains(removed_playlist)){
                                    blissful_available_playlists.remove(removed_playlist);
                                }
                            }


                            // find and add a potential replacer
                            Playlist replacer_playlist = heartache_available_playlists.getMax();

                            if (replacer_playlist.id != 0){
                                Song replacer = replacer_playlist.heartache_songs.deleteMax();
                                replacer.is_in_blend = true;
                                blend_songs.add(replacer);
                                replacer_h = replacer.id;
                                blend_heartache_limit--;
                                replacer_playlist.limits[0]--;
                                // update replacer song's playlist's heaps
                                Song new_max = replacer_playlist.heartache_songs.getMax();
                                Song former_min = replacer_playlist.heartache_blend_songs.getMin();
                                replacer_playlist.heartache_blend_songs.add(replacer);
                                // update main heaps
                                if (new_max.id == 0 || replacer_playlist.limits[0] == 0){
                                    heartache_available_playlists.deleteMax();
                                }
                                else {
                                    heartache_available_playlists.percolateDown(1);
                                }
                                if (former_min.id == 0){
                                    min_heartache_blend_songs_of_playlists.add(replacer);
                                }
                                else {
                                    min_heartache_blend_songs_of_playlists.replace(former_min, replacer);
                                }
                            }
                        }




                        // removed song participated in roadtrip category
                        if (removed_playlist.roadtrip_blend_songs.remove(removed_song)){
                            removed_r = removed_song.id;
                            removed_playlist.limits[1]++;
                            // update main's heaps
                            if (removed_playlist.limits[1] == 1 && removed_playlist.roadtrip_songs.size > 0){
                                roadtrip_available_playlists.add(removed_playlist);
                            }
                            blend_roadtrip_limit++;

                            Song new_min = removed_playlist.roadtrip_blend_songs.getMin();
                            if (new_min.id == 0){
                                min_roadtrip_blend_songs_of_playlists.remove(removed_song);
                            }
                            else if (removed_song.compareScore(new_min, 1) < 0){
                                min_roadtrip_blend_songs_of_playlists.replace(removed_song, new_min);
                            }
                            // update other score types' max heaps
                            if (removed_playlist.heartache_songs.contains(removed_song)){
                                removed_playlist.heartache_songs.remove(removed_song);
                                Song new_max = removed_playlist.heartache_songs.getMax();
                                if (new_max.id != 0){
                                    if (heartache_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 0) > 0){
                                        heartache_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (heartache_available_playlists.contains(removed_playlist)){
                                    heartache_available_playlists.remove(removed_playlist);
                                }
                            }

                            if (removed_playlist.blissful_songs.contains(removed_song)){
                                removed_playlist.blissful_songs.remove(removed_song);
                                Song new_max = removed_playlist.blissful_songs.getMax();
                                if (new_max.id != 0){
                                    if (blissful_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 2) > 0){
                                        blissful_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (blissful_available_playlists.contains(removed_playlist)){
                                    blissful_available_playlists.remove(removed_playlist);
                                }
                            }


                            // find and add a potential replacer
                            Playlist replacer_playlist = roadtrip_available_playlists.getMax();

                            if (replacer_playlist.id != 0){
                                Song replacer = replacer_playlist.roadtrip_songs.deleteMax();
                                replacer.is_in_blend = true;
                                blend_songs.add(replacer);
                                replacer_r = replacer.id;
                                blend_roadtrip_limit--;
                                replacer_playlist.limits[1]--;
                                // update replacer song's playlist's heaps
                                Song new_max = replacer_playlist.roadtrip_songs.getMax();
                                Song former_min = replacer_playlist.roadtrip_blend_songs.getMin();
                                replacer_playlist.roadtrip_blend_songs.add(replacer);
                                // update main heaps
                                if (new_max.id == 0 || replacer_playlist.limits[1] == 0){
                                    roadtrip_available_playlists.deleteMax();
                                }
                                else {
                                    roadtrip_available_playlists.percolateDown(1);
                                }
                                if (former_min.id == 0){
                                    min_roadtrip_blend_songs_of_playlists.add(replacer);
                                }
                                else {
                                    min_roadtrip_blend_songs_of_playlists.replace(former_min, replacer);
                                }
                            }
                        }



                        // removed song participated in blissful category
                        if (removed_playlist.blissful_blend_songs.remove(removed_song)){
                            removed_b = removed_song.id;
                            removed_playlist.limits[2]++;
                            // update main's heaps
                            if (removed_playlist.limits[2] == 1 && removed_playlist.blissful_songs.size > 0){
                                blissful_available_playlists.add(removed_playlist);
                            }
                            blend_blissful_limit++;

                            Song new_min = removed_playlist.blissful_blend_songs.getMin();
                            if (new_min.id == 0){
                                min_blissful_blend_songs_of_playlists.remove(removed_song);
                            }
                            else if (removed_song.compareScore(new_min, 2) < 0){
                                min_blissful_blend_songs_of_playlists.replace(removed_song, new_min);
                            }
                            // update other score types' max heaps
                            if (removed_playlist.roadtrip_songs.contains(removed_song)){
                                removed_playlist.roadtrip_songs.remove(removed_song);
                                Song new_max = removed_playlist.roadtrip_songs.getMax();
                                if (new_max.id != 0){
                                    if (roadtrip_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 1) > 0){
                                        roadtrip_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (roadtrip_available_playlists.contains(removed_playlist)){
                                    roadtrip_available_playlists.remove(removed_playlist);
                                }
                            }

                            if (removed_playlist.heartache_songs.contains(removed_song)){
                                removed_playlist.heartache_songs.remove(removed_song);
                                Song new_max = removed_playlist.heartache_songs.getMax();
                                if (new_max.id != 0){
                                    if (heartache_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 0) > 0){
                                        heartache_available_playlists.updatePlace(removed_playlist);
                                    }
                                }
                                else if (heartache_available_playlists.contains(removed_playlist)){
                                    heartache_available_playlists.remove(removed_playlist);
                                }
                            }



                            // find and add a potential replacer
                            Playlist replacer_playlist = blissful_available_playlists.getMax();

                            if (replacer_playlist.id != 0){
                                Song replacer = replacer_playlist.blissful_songs.deleteMax();
                                replacer.is_in_blend = true;
                                blend_songs.add(replacer);
                                replacer_b = replacer.id;
                                blend_blissful_limit--;
                                replacer_playlist.limits[2]--;
                                // update replacer song's playlist's heaps
                                Song new_max = replacer_playlist.blissful_songs.getMax();
                                Song former_min = replacer_playlist.blissful_blend_songs.getMin();
                                replacer_playlist.blissful_blend_songs.add(replacer);
                                // update main heaps
                                if (new_max.id == 0 || replacer_playlist.limits[2] == 0){
                                    blissful_available_playlists.deleteMax();
                                }
                                else {
                                    blissful_available_playlists.percolateDown(1);
                                }
                                if (former_min.id == 0){
                                    min_blissful_blend_songs_of_playlists.add(replacer);
                                }
                                else {
                                    min_blissful_blend_songs_of_playlists.replace(former_min, replacer);
                                }
                            }
                        }

                    }

                    // removed song was not in the blend
                    else{
                        removed_playlist.heartache_songs.remove(removed_song);
                        Song new_max = removed_playlist.heartache_songs.getMax();
                        if (new_max.id != 0){
                            if (heartache_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 0) > 0){
                                heartache_available_playlists.updatePlace(removed_playlist);
                            }
                        }
                        else if (heartache_available_playlists.contains(removed_playlist)){
                            heartache_available_playlists.remove(removed_playlist);
                        }

                        removed_playlist.roadtrip_songs.remove(removed_song);
                        new_max = removed_playlist.roadtrip_songs.getMax();
                        if (new_max.id != 0){
                            if (roadtrip_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 1) > 0){
                                roadtrip_available_playlists.updatePlace(removed_playlist);
                            }
                        }
                        else if (roadtrip_available_playlists.contains(removed_playlist)){
                            roadtrip_available_playlists.remove(removed_playlist);
                        }

                        removed_playlist.blissful_songs.remove(removed_song);
                        new_max = removed_playlist.blissful_songs.getMax();
                        if (new_max.id != 0){
                            if (blissful_available_playlists.contains(removed_playlist) && removed_song.compareScore(new_max, 2) > 0){
                                blissful_available_playlists.updatePlace(removed_playlist);
                            }
                        }
                        else if (blissful_available_playlists.contains(removed_playlist)){
                            blissful_available_playlists.remove(removed_playlist);
                        }
                    }

                    outwriter.write(String.format("%d %d %d\n%d %d %d\n", replacer_h, replacer_r, replacer_b, removed_h, removed_r, removed_b));
                    break;


                case "ASK":
                    MyBSTree tree = new MyBSTree();
                    for (Song s : blend_songs){
                        tree.add(s);
                    }
                    inorderPrint(tree.root, outwriter);
                    outwriter.write("\n");
                    break;
            }
        }
        case_file.close();
        outwriter.close();
    }

    public static void inorderPrint(MyBSTree.TreeNode node, PrintWriter outwriter){
        if (node == null){
            return;
        }
        inorderPrint(node.right, outwriter);
        outwriter.write(node.song.id + " ");
        inorderPrint(node.left, outwriter);

    }
}
