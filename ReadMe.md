**This is solution of the 'Crypto recommendation service' task from Pavlo Khshanovskyi**

**Info about endpoints you can read on the page http://localhost:8081/swagger-ui/ if you run it locally**


<details lang="java">
<summary>How to start:</summary>

<details lang="java">
<summary>In Intellij Idea:</summary>

1. Clone project.
2. Run via command from terminal
```
mvn clean install
```
or manually via interface

![Screenshot 2022-11-05 124527](https://user-images.githubusercontent.com/55089853/200116161-d893140e-d303-45ac-856f-0ee976328238.png)

3. Start it via CryptoRecommendationsServiceApplication#main
   ![Screenshot 2022-11-05 125834](https://user-images.githubusercontent.com/55089853/200116608-7d037dc6-f332-4a67-af9c-a1bfd49ece5f.png)


</details>

<details lang="java">
<summary>Docker:</summary>

```
docker pull epampavlokhshanovskyi/crypto-adviser
```

```
docker run -d --name crypto-recommenrations-service -p 8081:8081 epampavlokhshanovskyi/crypto-adviser
```

</details>


</details>

<details lang="java">
<summary>Task description:</summary>

[Crypto Recommendations Service.pdf](https://github.com/khshanovskyi/Crypto-Recommendations-Service/files/9943283/Crypto.Recommendations.Service.pdf)

![Screenshot 2022-11-05 093156](https://user-images.githubusercontent.com/55089853/200108439-6c3d1106-5743-4b8a-9452-80a8b66d85a4.png)
![Screenshot 2022-11-05 093253](https://user-images.githubusercontent.com/55089853/200108467-893df710-1f61-4f35-a792-29386e1b86e3.png)

</details>

<details lang="java">
<summary>Description of solution:</summary>

1. Application has strict format of the path tree with files where stored info about Crypto.
2. This tree looks like <br>
![Screenshot 2022-11-05 102246](https://user-images.githubusercontent.com/55089853/200110730-e96d861a-4516-4e73-96fb-237a5383461a.png)
3. in the 'crypto' folders persists folders where name represents like 'yyyy-MM'. These folders contain files with 
Crypto information. <br> Each file contains info for one month and yyyy-MM are the same with folder name where it stored.
4. Each file has strict format of data. File name have to start in uppercase with short name of the Crypto (Bitcoin -> BTC), 
then it should contains '_' symbol, then some additional info. Example -> 'BTC_values' <br>
File extension must be .csv <br>
So, the full file with name and extension example -> 'BTC_values.csv'
5. Files has strict format of inner data, example -> <br>
   timestamp,symbol,price<br>
   1641009600000,BTC,46813.21<br>
   1641020400000,BTC,46979.61


</details>

<details lang="java">
<summary>TODO list:</summary>

1. Security for CacheEvictionController (authorize that user is admin)
2. Some DB for saving users
3. Endpoint for uploading files + authorization that user is admin
4. Service for uploading files that will be able to recognize where to put file (should compare LocalDate from file
with Dates from the folder names)

</details>

