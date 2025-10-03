# PostgreSQL Database Container
FROM postgres:18

# Set environment variables
ENV POSTGRES_DB=Facebook
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=admin

# Expose PostgreSQL port
EXPOSE 5432

# Create volume for data persistence
VOLUME ["/var/lib/postgresql/data"]

# Start PostgreSQL
CMD ["postgres"]
