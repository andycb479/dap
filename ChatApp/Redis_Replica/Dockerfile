FROM redis:6.2.1

COPY Redis_Replica/redis.conf /usr/local/etc/redis/redis.conf
COPY Redis_Replica/users.acl /etc/redis/users.acl

EXPOSE 6379

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]