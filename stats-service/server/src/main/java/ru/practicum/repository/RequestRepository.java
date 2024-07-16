package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.HitResponseDto;
import ru.practicum.model.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<HitResponseDto> getAllRequestsWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<HitResponseDto> getUniqueIpRequestsWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<HitResponseDto> getUniqueIpRequestsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<HitResponseDto> getAllRequestsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "AND r.ip = ?4 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<HitResponseDto> getAllRequestsWithUriByIp(LocalDateTime start, LocalDateTime end, List<String> uris, String ip);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "AND r.ip = ?4 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<HitResponseDto> getUniqueIpRequestsWithUriByIp(LocalDateTime start, LocalDateTime end, List<String> uris, String ip);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.ip = ?3 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<HitResponseDto> getUniqueIpRequestsWithoutUriByIp(LocalDateTime start, LocalDateTime end, String ip);

    @Query(value = "SELECT new ru.practicum.HitResponseDto(a.name, r.uri, COUNT(r.ip)) " +
            "FROM Request as r " +
            "LEFT JOIN App as a ON a.id = r.app.id " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.ip = ?3 " +
            "GROUP BY a.name, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<HitResponseDto> getAllRequestsWithoutUriByIp(LocalDateTime start, LocalDateTime end, String ip);
}
