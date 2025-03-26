using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class User
{
    public string UserId { get; set; } = null!;

    public string Name { get; set; } = null!;

    public string Password { get; set; } = null!;

    public string? Phone { get; set; }

    public string? PhotoPath { get; set; }

    public virtual ICollection<History> Histories { get; set; } = new List<History>();

    public virtual ICollection<HomeUser> HomeUsers { get; set; } = new List<HomeUser>();

    public virtual ICollection<UserAuthority> UserAuthorities { get; set; } = new List<UserAuthority>();
}
