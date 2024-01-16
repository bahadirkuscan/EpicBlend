import java.util.HashMap;

public class MaxHeapPlaylist {
    private int size = 0;
    public Playlist[] playlists;
    public HashMap<Integer,Integer> index_map = new HashMap<>();
    private final int type;


    MaxHeapPlaylist(int capacity, int type){
        playlists = new Playlist[capacity + 1];
        this.type = type;
    }


    public void percolateDown(int hole_index){
        int child = hole_index * 2;
        Playlist child_playlist;
        Playlist temp = playlists[hole_index];

        while (child <= size){
            child_playlist = playlists[child];
            if (child != size && child_playlist.comparePlaylist(playlists[child+1], type) < 0){
                child++;
                child_playlist = playlists[child];
            }
            if (temp.comparePlaylist(child_playlist, type) > 0){
                break;
            }
            playlists[hole_index] = child_playlist;
            index_map.replace(child_playlist.id, hole_index);
            hole_index = child;
            child *= 2;
        }
        playlists[hole_index] = temp;
        index_map.replace(temp.id, hole_index);
    }


    public void add(Playlist playlist){
        int hole_index = ++size;
        playlists[hole_index] = playlist;
        percolateUp(hole_index);
    }

    public Playlist getMax(){
        if (size == 0){
            return new Playlist();
        }
        return playlists[1];
    }

    public boolean remove(Playlist playlist){
        Integer remove_index = index_map.remove(playlist.id);
        if (remove_index == null){
            return false;
        }
        playlists[remove_index] = playlists[size--];
        if(remove_index > 1 && playlists[remove_index].comparePlaylist(playlists[remove_index/2], type) > 0){
            percolateUp(remove_index);
        }
        else {
            percolateDown(remove_index);
        }
        return true;
    }

    public Playlist deleteMax(){
        if (size == 0){
            return new Playlist();
        }
        Playlist playlist = playlists[1];
        index_map.remove(playlist.id);
        playlists[1] = playlists[size--];
        percolateDown(1);
        return playlist;
    }

    public void buildHeap(){
        for( int i = size / 2; i > 0; i-- ){
            percolateDown(i);
        }
    }

    public void append(Playlist playlist){
        int index = ++size;
        playlists[index] = playlist;
        index_map.put(playlist.id, index);
    }
    public void percolateUp(int hole_index){
        Playlist playlist = playlists[hole_index];
        int parent = hole_index / 2;
        Playlist parent_playlist;
        while (parent > 0){
            parent_playlist = playlists[parent];
            if (playlist.comparePlaylist(parent_playlist, type) > 0){
                playlists[hole_index] = parent_playlist;
                index_map.put(parent_playlist.id, hole_index);
            }
            else{
                break;
            }
            hole_index /= 2;
            parent /= 2;
        }
        playlists[hole_index] = playlist;
        index_map.put(playlist.id, hole_index);
    }

    public boolean contains(Playlist playlist){
        return index_map.containsKey(playlist.id);
    }

    public void updatePlace(Playlist playlist){
        int index = index_map.get(playlist.id);
        if (index > 1 && playlist.comparePlaylist(playlists[index/2], type) > 0){
            percolateUp(index);
        }
        else {
            percolateDown(index);
        }
    }
}
