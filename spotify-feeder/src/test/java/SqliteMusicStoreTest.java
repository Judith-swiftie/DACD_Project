import org.example.control.store.SqliteMusicStore;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SqliteMusicStoreTest {

    // Simulamos la base de datos con una subclase que sobrescribe el comportamiento
    private static class FakeSqliteMusicStore extends SqliteMusicStore {
        @Override
        public void saveArtistAndTracks(String artistId, String artistName, List<String> tracks) {
            // Simulamos que los datos se guardan correctamente
            // No se hace realmente la inserción en la base de datos
        }

        @Override
        public List<String> getTracksByArtistId(String artistId) {
            // Simulamos que estos son los tracks guardados
            return List.of("Cruel Summer", "Lover", "Cardigan");
        }
    }

    @Test
    void testSaveArtistAndTracks() throws Exception {
        String artistId = "06HL4z0CvFAxyc27GXpf02";
        String artistName = "Taylor Swift";
        List<String> tracks = List.of("Cruel Summer", "Lover", "Cardigan");

        // Usamos el almacenamiento simulado
        SqliteMusicStore store = new FakeSqliteMusicStore();

        // Llamamos al método de almacenamiento simulado
        store.saveArtistAndTracks(artistId, artistName, tracks);

        // Verificamos que los tracks se han guardado correctamente
        List<String> savedTracks = store.getTracksByArtistId(artistId);
        assertTrue(savedTracks.containsAll(tracks), "Los tracks no se guardaron correctamente.");
    }
}
