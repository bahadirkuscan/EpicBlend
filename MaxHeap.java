import java.util.HashMap;

public class MaxHeap {
    public int size = 0;
    public Song[] songs;
    public HashMap<Integer,Integer> index_map = new HashMap<>();
    private final int type;
    private int capacity = 100;

    MaxHeap(int type){
        this(100, type);
    }

    MaxHeap(int capacity, int type){
        songs = new Song[capacity];
        this.type = type;
    }


    private void percolateDown(int hole_index){
        int child = hole_index * 2;
        Song child_song;
        Song temp = songs[hole_index];

        while (child <= size){
            child_song = songs[child];
            if (child != size && child_song.compareScore(songs[child+1], type) < 0){
                child++;
                child_song = songs[child];
            }
            if (temp.compareScore(child_song, type) > 0){
                break;
            }
            songs[hole_index] = child_song;
            index_map.replace(child_song.id, hole_index);
            hole_index = child;
            child *= 2;
        }
        songs[hole_index] = temp;
        index_map.replace(temp.id, hole_index);
    }


    public void add(Song song){
        if (size + 1 == capacity){
            resize();
        }
        int hole_index = ++size;
        songs[hole_index] = song;
        percolateUp(hole_index);
    }

    public Song getMax(){
        if (size == 0){
            return new Song();
        }
        return songs[1];
    }

    public boolean remove(Song song){
        Integer remove_index = index_map.remove(song.id);
        if (remove_index == null){
            return false;
        }
        songs[remove_index] = songs[size--];
        if(remove_index > 1 && songs[remove_index].compareScore(songs[remove_index/2], type) > 0){
            percolateUp(remove_index);
        }
        else {
            percolateDown(remove_index);
        }
        return true;
    }

    public Song deleteMax(){
        if (size == 0){
            return new Song();
        }
        Song song = songs[1];
        index_map.remove(song.id);
        songs[1] = songs[size--];
        percolateDown(1);
        return song;
    }

    public void buildHeap(){
        for( int i = size / 2; i > 0; i-- ){
            percolateDown(i);
        }
    }

    public void append(Song song){
        if (size + 1 == capacity){
            resize();
        }
        int index = ++size;
        songs[index] = song;
        index_map.put(song.id, index);
    }
    public void percolateUp(int hole_index){
        Song song = songs[hole_index];
        int parent = hole_index / 2;
        Song parent_song;
        while (parent > 0){
            parent_song = songs[parent];
            if (song.compareScore(parent_song, type) > 0){
                songs[hole_index] = parent_song;
                index_map.put(parent_song.id, hole_index);
            }
            else{
                break;
            }
            hole_index /= 2;
            parent /= 2;
        }
        songs[hole_index] = song;
        index_map.put(song.id, hole_index);
    }


    public void replace(Song replaced_song, Song replacer){
        int index = index_map.remove(replaced_song.id);
        songs[index] = replacer;
        index_map.put(replacer.id, index);
        if (index > 1 && replacer.compareScore(songs[index / 2], type) > 0){
            percolateUp(index);
            return;
        }
        percolateDown(index);
    }

    public void resize(){
        capacity *= 2;
        Song[] new_array = new Song[capacity];
        System.arraycopy(songs,1,new_array,1, size);
        songs = new_array;
    }

    public boolean contains(Song song){
        return index_map.containsKey(song.id);
    }
}
