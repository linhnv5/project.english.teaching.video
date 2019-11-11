package topica.linhnv5.video.teaching.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import topica.linhnv5.video.teaching.model.Music;

@Repository
public interface MusicRepository extends CrudRepository<Music, Long> {

	/**
	 * Select music by given chart name and artist name
	 * @param chart chart name
	 * @param artist artist name
	 * @return the music
	 */
	@Query("SELECT obj FROM Music obj WHERE obj.chart=:chart AND obj.artist=:artist")
	public Music findByChartAndArtist(@Param("chart") String chart, @Param("artist") String artist);

}
