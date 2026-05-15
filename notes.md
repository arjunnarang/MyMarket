## Command to run postgresql in docker

````
docker run --name postgres-db -e POSTGRES_USER=Arjun -e POSTGRES_PASSWORD=dummypassword -e POSTGRES_DB=product-db -p 5433:5432 -d postgres:17
``````
/api/v1/products/create
/api/v1/products/update
/api/v1/products/fetch/id
/api/v1/products/fetchAllProducts
/api/v1/products/fetchByCategoryId/id