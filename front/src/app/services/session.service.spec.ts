import { TestBed, fakeAsync } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;
  const validCredentials: SessionInformation = {
    token: 'your_mocked_token',
    type: 'your_mocked_type',
    id: 123,
    username: 'jean@gmail.com',
    firstName: 'Jean',
    lastName: 'DUPONT',
    admin: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  /*   it('should log the user when credentials are valid'), fakeAsync(() => {});
   */
});
