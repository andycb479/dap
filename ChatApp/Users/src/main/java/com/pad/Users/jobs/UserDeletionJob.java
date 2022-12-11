package com.pad.Users.jobs;

import com.pad.Users.UserRepository;
import com.pad.Users.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserDeletionJob {
  private int userDeletionJobFrequency = 3600;

  @Autowired UserRepository userRepository;

  @Autowired
  public UserDeletionJob(Environment env) {
    userDeletionJobFrequency =
        Integer.parseInt(Objects.requireNonNull(env.getProperty("user.deletion.job.frequency")));
  }

  @EventListener(ApplicationReadyEvent.class)
  public void clean() {
    Runnable cleaningJobRunnable =
        () -> {
          while (true) {

            try {
              Thread.sleep(userDeletionJobFrequency);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }

            log.info("Cleaning job executing...");

            List<User> users = userRepository.findAll();
            users.forEach(
                user -> {
                  if (!user.isActive()) {
                    log.info("Found user with status inactive. Removing the user.");
                    userRepository.delete(user);
                    log.info("User with ID " + user.getId() + " removed successfully from the DB.");
                  }
                });
          }
        };

    cleaningJobRunnable.run();
  }
}
