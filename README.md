# neueda URL app

Access URL: http://localhost:8080/shrink?url=www.google.com (to shrink the URL - will return the shrink new URL)

curl -L http://localhost:8080/redirect?shortUrl=http://san.ri/WfS1XOa to get the redirect Original URL from encoded URL


1. Please run the jar using the below command:

java -jar restservice-0.0.1-SNAPSHOT.jar (would need the java installation)

2. The persistence storage cache will be present in:

/tmp/out.txt (on linux)

C:\temp\out.txt (on windows)

3. Assumptions are:

Website address provided in string format with no blank spaces

In-memory cache (ehcache) is used during the jvm run to fetch the values which are already processed so that unneccessary function call is avoided.

As the values are processed they are being written to persistence store hence they survive the JVM restarts and persistence cache is built from the same during spring initialization.

4. Results are:

The shorturl is::http://san.ri/Hxwv3ui (the value is fetched/computed or from inmemory cache)

Url found(from disk cache)::http://san.ri/tXGF9MI (the value is fetched as it was per processsed from persistence storage)

5. Writing to persistance cache is not within the URL creation processing hence does not effect the performance.

6. For any support queries the server logs can be checked which provide the computed URL's and also give persistent cache details (which can be stopped as its loaded just once on startup)

7. Docker image was build (op the parent directory of target) using:

docker build -t goenka811/neueda-app .

8. Can run using docker by:

docker run -v ${PWD}/output:/tmp -p 8080:8080 goenka811/neueda-app

You can reach me on rishabhgoenka@gmail.com if any queries on the same..
