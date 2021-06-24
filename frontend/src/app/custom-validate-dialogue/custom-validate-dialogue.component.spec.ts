import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomValidateDialogueComponent } from './custom-validate-dialogue.component';

describe('CustomValidateDialogueComponent', () => {
  let component: CustomValidateDialogueComponent;
  let fixture: ComponentFixture<CustomValidateDialogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomValidateDialogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomValidateDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
