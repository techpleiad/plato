import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchProfileInputComponent } from './branch-profile-input.component';

describe('BranchProfileInputComponent', () => {
  let component: BranchProfileInputComponent;
  let fixture: ComponentFixture<BranchProfileInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BranchProfileInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchProfileInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
