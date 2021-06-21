import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsistencyAcrossProfileDialogueComponent } from './consistency-across-profile-dialogue.component';

describe('ConsistencyAcrossProfileDialogueComponent', () => {
  let component: ConsistencyAcrossProfileDialogueComponent;
  let fixture: ComponentFixture<ConsistencyAcrossProfileDialogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsistencyAcrossProfileDialogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsistencyAcrossProfileDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
