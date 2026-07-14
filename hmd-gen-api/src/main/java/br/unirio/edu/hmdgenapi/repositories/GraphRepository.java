package br.unirio.edu.hmdgenapi.repositories;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import br.unirio.edu.hmdgenapi.models.Graph;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GraphRepository extends FirestoreReactiveRepository<Graph> {

    Flux<Graph> findByOrderByNameAscTypeAsc();

    Flux<Graph> findByType(String type);

    Flux<Graph> findByPackageOnly(Boolean packageOnly);

    Flux<Graph> findByTypeAndPackageOnly(String type, Boolean packageOnly);

    Mono<Graph> findByTypeAndNameAndPackageOnly(String type, String name, Boolean packageOnly);

}