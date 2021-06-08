import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayServicesComponent } from './display-services.component';

describe('DisplayServicesComponent', () => {
  let component: DisplayServicesComponent;
  let fixture: ComponentFixture<DisplayServicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DisplayServicesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DisplayServicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
