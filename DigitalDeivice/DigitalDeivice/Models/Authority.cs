using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class Authority
{
    public string AuthorityId { get; set; } = null!;

    public string NameAuthority { get; set; } = null!;

    public string? Description { get; set; }

    public virtual ICollection<UserAuthority> UserAuthorities { get; set; } = new List<UserAuthority>();
}
