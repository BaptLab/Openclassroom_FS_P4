import { HttpClientModule } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs'; // Import EMPTY from RxJS
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flush,
  tick,
} from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { MatSnackBar } from '@angular/material/snack-bar';

import { MeComponent } from './me.component';
import { Router } from '@angular/router';
import { User } from 'src/app/interfaces/user.interface';
import { of } from 'rxjs';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    logOut: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        NoopAnimationsModule,
        MatInputModule,
        RouterTestingModule,
      ],
      providers: [{ provide: SessionService, useValue: mockSessionService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the previous page', fakeAsync(() => {
    const spy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    tick();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  }));

  it('should get the user on init', fakeAsync(() => {
    const mockUser: User = {
      id: 1,
      email: 'john@gmail.com',
      lastName: 'Doe',
      firstName: 'John',
      admin: true,
      password: 'pwd',
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    const userServiceGetByIdSpy = jest
      .spyOn(component['userService'], 'getById')
      .mockReturnValue(of(mockUser));

    component.ngOnInit();
    tick();
    fixture.detectChanges();

    expect(userServiceGetByIdSpy).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);

    userServiceGetByIdSpy.mockRestore();
  }));

  it('should delete the user', fakeAsync(() => {
    const routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate');
    const userServiceDeleteSpy = jest
      .spyOn(component['userService'], 'delete')
      .mockImplementation((id: string) => of(null));
    const matSnackBarOpenSpy = jest.spyOn(component['matSnackBar'], 'open');

    component.delete();
    tick();
    fixture.detectChanges();
    flush();

    expect(userServiceDeleteSpy).toHaveBeenCalledWith('1');
    expect(matSnackBarOpenSpy).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      {
        duration: 3000,
      }
    );
    expect(component['sessionService'].logOut).toHaveBeenCalled();
    expect(routerNavigateSpy).toHaveBeenCalledWith(['/']);

    userServiceDeleteSpy.mockRestore();
    matSnackBarOpenSpy.mockRestore();
    routerNavigateSpy.mockRestore();
  }));
});
