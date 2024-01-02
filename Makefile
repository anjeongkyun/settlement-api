test:
	docker-compose up -d
	./gradlew unit-test:test
	docker-compose down
