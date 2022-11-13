package com.pad.Gateway.services;

import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.impl.UsersServiceImpl;
import com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer;
import messages.GenericReply;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import users.User;
import users.UserIdRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsersServiceTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  UsersRequestsLoadBalancer loadBalancer;

  UserService userService = new UsersServiceImpl();

  @Mock GenericReply reply;

  @Before
  public void setUp() throws Exception {
    loadBalancer = mock(UsersRequestsLoadBalancer.class);
    Whitebox.setInternalState(userService, "loadBalancer", loadBalancer);
  }

  @Test
  public void testGetUser() {
    int id = 1;
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(id).build();

    User user = User.newBuilder().setUserId(1).build();

    when(loadBalancer.getNextAvailableService().getUserRequest(request)).thenReturn(user);

    UserDto userDto = userService.getUser(id);

    assertEquals(id, userDto.getUserId());
  }

  @Test
  public void testSendChatRequest() throws Exception {}
}
