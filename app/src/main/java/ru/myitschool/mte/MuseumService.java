package ru.myitschool.mte;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MuseumService {

    /*
     В этом интерфейсе были описаны следующие два HTTPS запроса

      * для запроса списка объектов по автору
      * и конкретного объекта по номеру.
      
      Пример запросов, соответствующих  приведенному ранее примеру:

https://collectionapi.metmuseum.org/public/collection/v1/search?q=Van%20Gogh

https://collectionapi.metmuseum.org/public/collection/v1/objects/459123

Дополнительную информацию по веб API «The Metropolitan Museum of Art» можно получить по адресу:
 https://metmuseum.github.io/
     */

    @GET("/api/users?")
    Call<Objects> findObjectByAuthor(String toString);

    @GET("/api/unknown")
    Call<ArtObject> getObject(int parseInt);
}
