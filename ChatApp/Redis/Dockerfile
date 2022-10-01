FROM redis:6.2.1

COPY Redis/redis.conf /usr/local/etc/redis/redis.conf
COPY Redis/users.acl /etc/redis/users.acl

EXPOSE 6379

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]